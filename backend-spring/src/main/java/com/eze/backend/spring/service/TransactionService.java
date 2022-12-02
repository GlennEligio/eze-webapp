package com.eze.backend.spring.service;

import com.eze.backend.spring.enums.TxStatus;
import com.eze.backend.spring.exception.ApiException;
import com.eze.backend.spring.model.*;
import com.eze.backend.spring.repository.TransactionRepository;
import com.eze.backend.spring.util.ObjectIdGenerator;
import com.eze.backend.spring.util.TimeStampProvider;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class TransactionService implements IService<Transaction>, IExcelService<Transaction> {

    private final TransactionRepository txRepo;
    private final EquipmentService eqService;
    private final ProfessorService profService;
    private final StudentService studentService;
    private final ObjectIdGenerator idGenerator;
    private final TimeStampProvider timeStampProvider;

    public TransactionService(TransactionRepository txRepo, EquipmentService eqService, ProfessorService profService, StudentService studentService, ObjectIdGenerator idGenerator, TimeStampProvider timeStampProvider) {
        this.txRepo = txRepo;
        this.eqService = eqService;
        this.profService = profService;
        this.studentService = studentService;
        this.idGenerator = idGenerator;
        this.timeStampProvider = timeStampProvider;
    }

    @Override
    public List<Transaction> getAll() {
        log.info("Fetching all transactions");
        return txRepo.findAll();
    }

    @Override
    public List<Transaction> getAllNotDeleted() {
        log.info("Fetching all non deleted transactions");
        return txRepo.findAllNotDeleted();
    }

    @Override
    public Transaction get(Serializable code) {
        log.info("Fetching transaction with code {}", code);
        return txRepo.findByTxCode(code.toString()).orElseThrow(() -> new ApiException(notFound(code), HttpStatus.NOT_FOUND));
    }

    @Transactional
    @Override
    public Transaction create(Transaction transaction) {
        log.info("Creating transaction {}", transaction);
        if (transaction.getTxCode() != null) {
            Optional<Transaction> opTx = txRepo.findByTxCode(transaction.getTxCode());
            if (opTx.isPresent()) {
                throw new ApiException(alreadyExist(transaction.getTxCode()), HttpStatus.BAD_REQUEST);
            }
        }

        // Populate transaction's equipments
        log.info("Getting complete equipment information of the borrowed equipments");
        List<Equipment> equipments = transaction.getEquipments().parallelStream()
                .map(eq -> eqService.get(eq.getEquipmentCode()))
                .toList();

        // Check if any equipments is not duplicable and is already borrowed
        log.info("Checking if any equipments is borrowed already");
        checkEqAlreadyBorrowed(equipments);

        // Set the isBorrowed of non-duplicable equipment to true
        log.info("Updating all non-duplicable equipments' borrowed status to true");
        equipments.forEach(e -> {
            if (Boolean.FALSE.equals(e.getIsDuplicable())) {
                e.setIsBorrowed(true);
            }
            eqService.update(e, e.getEquipmentCode());
        });

        // Populate Equipment and Equipment history data of transaction
        // For Equipment, remove those that is duplicable (i.e. only maintain those that is not duplicable)
        log.info("Populating eqHist and eqs of Transaction");
        transaction.setEquipmentsHist(new ArrayList<>(equipments));
        transaction.setEquipments(new ArrayList<>(equipments.stream().filter(e -> !e.getIsDuplicable()).toList()));

        // Populate Professor and Student data of transaction
        log.info("Populating professor of Transaction");
        String profName = transaction.getProfessor().getName();
        transaction.setProfessor(profService.get(profName));

        log.info("Populating student of Transaction");
        String studentNumber = transaction.getBorrower().getStudentNumber();
        transaction.setBorrower(studentService.get(studentNumber));

        // Set the transaction code
        log.info("Generating txCode for Transaction");
        transaction.setTxCode(idGenerator.createId());

        // Set transaction status to pending
        log.info("Setting status to PENDING of Transaction");
        transaction.setStatus(TxStatus.PENDING);

        // Set deleteFlag to false
        log.info("Setting deleteFlag of Transaction to false");
        transaction.setDeleteFlag(false);

        // Add borrowed at in case its null
        log.info("Add getBorrowedAt date to Transaction");
        if (transaction.getBorrowedAt() == null) transaction.setBorrowedAt(timeStampProvider.getNow());
        return txRepo.save(transaction);
    }

    @Transactional
    @Override
    public Transaction update(Transaction transaction, Serializable code) {
        log.info("Updating transaction with code {} using {}", code, transaction);
        Transaction tx = txRepo.findByTxCode(code.toString()).orElseThrow(() -> new ApiException(notFound(code), HttpStatus.NOT_FOUND));

        if (transaction.getEquipments() != null) {
            List<Equipment> eqs = transaction.getEquipments().parallelStream()
                    .map(eq -> eqService.get(eq.getEquipmentCode()))
                    .toList();

            checkEqAlreadyBorrowed(eqs);
            transaction.setEquipments(new ArrayList<>(eqs.stream().filter(e -> !e.getIsDuplicable()).toList()));
        }

        if (transaction.getProfessor() != null) {
            String profName = transaction.getProfessor().getName();
            transaction.setProfessor(profService.get(profName));
        }

        if (transaction.getBorrower() != null) {
            String studentNumber = transaction.getBorrower().getStudentNumber();
            transaction.setBorrower(studentService.get(studentNumber));
        }

        tx.update(transaction);

        return txRepo.save(tx);
    }

    @Override
    @Transactional
    public void delete(Serializable code) {
        log.info("Deleting transaction with {}", code);
        Optional<Transaction> transactionOptional = txRepo.findByTxCode(code.toString());
        if (transactionOptional.isEmpty()) {
            throw new ApiException(notFound(code), HttpStatus.NOT_FOUND);
        }
        Transaction transaction = transactionOptional.get();
        if (Boolean.TRUE.equals(transaction.getDeleteFlag())) {
            throw new ApiException("Transaction is already soft deleted", HttpStatus.BAD_REQUEST);
        }
        returnAllEquipments(transaction);
        txRepo.delete(transaction);
    }

    @Override
    @Transactional
    public void softDelete(Serializable code) {
        log.info("Soft deleting transaction with {}", code);
        Transaction transaction = txRepo.findByTxCode(code.toString()).orElseThrow(() -> new ApiException(notFound(code), HttpStatus.NOT_FOUND));
        if (transaction.getDeleteFlag()) {
            throw new ApiException("Transaction is already soft deleted", HttpStatus.BAD_REQUEST);
        }
        returnAllEquipments(transaction);
        txRepo.softDelete(transaction.getTxCode());
    }

    @Override
    public String notFound(Serializable code) {
        return "No transaction with code " + code + " was found";
    }

    @Override
    public String alreadyExist(Serializable code) {
        return "Transaction with code " + code + " already exist";
    }

    @Override
    @Transactional
    public int addOrUpdate(@Valid List<Transaction> transactions, boolean overwrite) {
        log.info("Performing batch update for Transactions");
        int itemsAffected = 0;
        for (Transaction transaction : transactions) {
            Optional<Transaction> transactionOptional = txRepo.findByTxCode(transaction.getTxCode());
            if (transactionOptional.isEmpty()) {
                txRepo.save(transaction);
                itemsAffected++;
            } else {
                if (overwrite) {
                    Transaction oldTx = transactionOptional.get();
                    if (!oldTx.customEquals(transaction)) {
                        oldTx.update(transaction);
                        txRepo.save(oldTx);
                        itemsAffected++;
                    }
                }
            }
        }
        return itemsAffected;
    }

    public void checkEqAlreadyBorrowed(List<Equipment> equipments) {
        // Check if any equipments is not duplicable and is already borrowed
        List<Equipment> eqAlreadyBorrowed = equipments.parallelStream()
                .filter(eq -> !eq.getIsDuplicable())
                .filter(Equipment::getIsBorrowed)
                .toList();

        // If there is such equipment, list them and show them in response error
        if (!eqAlreadyBorrowed.isEmpty()) {
            String eqCodes = equipments.stream()
                    .map(Equipment::getEquipmentCode)
                    .reduce("", (ids, eqId) -> ids + ", " + eqId);
            throw new ApiException("The following items is already borrowed: " + eqCodes, HttpStatus.BAD_REQUEST);
        }
    }

    @Transactional
    public Transaction returnEquipments(String borrower, String professor, List<String> equipmentsBarcode) {
        log.info("Returning equipments with barcodes {}, for borrower {}, and professor {}", equipmentsBarcode, borrower, professor);
        List<Equipment> eqs = new ArrayList<>();
        for (String barcode : equipmentsBarcode) {
            eqs.add(eqService.getByBarcode(barcode));
        }
        log.info("Equipments found: {}", eqs.stream().map(Equipment::toEquipmentDto).toList());
        // Filter transactions so only those that match the borrower, professor, and contains all equipments from list of barcodes
        Transaction transactionMatch = getTransactionMatch(txRepo.findAll(), borrower, professor, equipmentsBarcode, eqs);

        log.info("Got the matching transaction {}", Transaction.toTransactionListDto(transactionMatch));
        // Filter new transaction equipments so that only those that is NOT included in equipments return is left
        List<Equipment> newEqs = new ArrayList<>(transactionMatch.getEquipments().stream()
                .filter(e -> !eqs.contains(e)).toList());
        log.info("Removed the returned eqs in the transaction");
        // Also change the non-duplicable equipments isBorrowed status to false
        eqs.forEach(e -> {
            if (Boolean.FALSE.equals(e.getIsDuplicable())) {
                e.setIsBorrowed(false);
            }
            eqService.update(e, e.getEquipmentCode());
        });
        log.info("Updated the equipments isBorrowed");

        // Set the transaction's new equipments list
        transactionMatch.setEquipments(newEqs);
        log.info("Updated the transaction equipments");
        // Change the returnedAt value if newEqs is empty
        if (newEqs.isEmpty()) transactionMatch.setReturnedAt(timeStampProvider.getNow());
        log.info("Update the transaction returnedAt value");
        log.info("New transaction {}", Transaction.toTransactionDto(transactionMatch));
        return txRepo.save(transactionMatch);
    }

    public Transaction getTransactionMatch(List<Transaction> transactions, String borrower, String professor, List<String> equipmentsBarcode, List<Equipment> eqs) {
        return transactions.stream()
                .filter(t -> t.getBorrower().getStudentNumber().equalsIgnoreCase(borrower))
//                .peek(t -> log.info("Transaction with same borrower {}", t))
                .filter(t -> t.getProfessor().getName().equalsIgnoreCase(professor))
//                .peek(t -> log.info("Transaction with same professor {}", t))
                .filter(t -> t.getReturnedAt() == null)
//                .peek(t -> log.info("Transaction with no returned yet {}", t))
                .filter(t -> t.getEquipments().containsAll(eqs))
//                .peek(t -> log.info("Transaction whose equipments contains all given eqs {}", t))
                .findFirst()
                .orElseThrow(() -> new ApiException(String.format("No transaction found that matches the following: Borrower %s, Professor %s, Barcodes %s", borrower, professor, equipmentsBarcode), HttpStatus.NOT_FOUND));
    }

    private void returnAllEquipments(Transaction transaction) {
        // Extract equipments in the transaction and set isBorrowed to false
        List<Equipment> equipment = transaction.getEquipments().stream()
                .filter(e -> !e.getIsDuplicable())
                .map(e -> {
                    e.setIsBorrowed(false);
                    return e;
                })
                .toList();
        // Save each equipment to update
        equipment.forEach(e -> eqService.update(e, e.getEquipmentCode()));
        // Set the transaction's eqList to new one and save
        transaction.setEquipments(new ArrayList<>(equipment));
        txRepo.save(transaction);
    }

    @Override
    public ByteArrayInputStream listToExcel(List<Transaction> transactions) {
        List<String> columnName = List.of("Transaction Code", "Equipment", "Borrower", "Year and Section", "Professor", "Borrowed At", "Returned At", "Status", "Is Returned", "Delete flag");
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Transactions");

            // Creating header row
            Row row = sheet.createRow(0);
            for (int i = 0; i < columnName.size(); i++) {
                row.createCell(i).setCellValue(columnName.get(i));
            }

            // Populating the Excel file with data
            int counter = 0;
            for (Transaction transaction : transactions) {
                // Create transaction data row per equipment, will cause redundancy
                for (int j = 0; j < transaction.getEquipmentsHist().size(); j++) {
                    Equipment equipment = transaction.getEquipmentsHist().get(j);
                    Row dataRow = sheet.createRow(counter + 1);

                    dataRow.createCell(0).setCellValue(transaction.getTxCode());
                    dataRow.createCell(1).setCellValue(equipment.getEquipmentCode());
                    dataRow.createCell(2).setCellValue(transaction.getBorrower().getStudentNumber());
                    dataRow.createCell(3).setCellValue(transaction.getBorrower().getYearAndSection().getSectionName());
                    dataRow.createCell(4).setCellValue(transaction.getProfessor().getName());
                    dataRow.createCell(5).setCellValue(transaction.getBorrowedAt().toString());
                    if (transaction.getReturnedAt() != null) {
                        dataRow.createCell(6).setCellValue(transaction.getReturnedAt().toString());
                    }
                    dataRow.createCell(7).setCellValue(transaction.getStatus().getName());

                    // Checks if the equipment is still in current Equipment list of Transaction
                    dataRow.createCell(8).setCellValue(transaction.getEquipments().contains(equipment));
                    dataRow.createCell(9).setCellValue(transaction.getDeleteFlag());
                    counter++;
                }
            }


            // Making size of the column auto resize to fit data
            for (int i = 0; i < columnName.size(); i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (IOException e) {
            throw new ApiException("Something went wrong when converting transactions to excel file", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<Transaction> excelToList(MultipartFile file) {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Map<String, Transaction> transactionMap = new HashMap<>();

            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                Row row = sheet.getRow(i);
                String transactionCode = row.getCell(0).getStringCellValue();

                // check if the transaction is already present
                // if it is, just add the equipment data
                // if not, create new transaction and add to the HashMap
                if (transactionMap.get(transactionCode) == null) {
                    Transaction transaction = new Transaction();
                    transaction.setTxCode(transactionCode);

                    // get the equipment in this row
                    String eqCode = row.getCell(1).getStringCellValue();
                    Equipment equipment = eqService.get(eqCode);

                    // add equipment in transaction's eqHist
                    transaction.setEquipmentsHist(new ArrayList<>(List.of(equipment)));

                    // add the equipment is eq if isReturned is false
                    Boolean isReturned = row.getCell(8).getBooleanCellValue();

                    // Add to equipments as well if its not returned yet and is not duplicable
                    if (!equipment.getIsDuplicable() && !isReturned) {
                        transaction.setEquipments(new ArrayList<>(List.of(equipment)));
                    } else {
                        transaction.setEquipments(new ArrayList<>());
                    }

                    // get the student number in this row
                    String studentNumber = row.getCell(2).getStringCellValue();
                    Student student = studentService.get(studentNumber);
                    transaction.setBorrower(student);

                    // get the professor of this transaction
                    String professorName = row.getCell(4).getStringCellValue();
                    Professor professor = profService.get(professorName);
                    transaction.setProfessor(professor);

                    // get the borrowed at info
                    String borrowedAt = row.getCell(5).getStringCellValue();
                    transaction.setBorrowedAt(LocalDateTime.parse(borrowedAt));

                    // get the returned at info if its present
                    String returnedAt = row.getCell(6, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();
                    if (returnedAt != null && !returnedAt.equals(""))
                        transaction.setReturnedAt(LocalDateTime.parse(returnedAt));

                    // get the status info if its present
                    String status = row.getCell(7).getStringCellValue();
                    transaction.setStatus(TxStatus.valueOf(status));

                    // get the isDeleteFlag
                    Boolean deleteFlag = row.getCell(9).getBooleanCellValue();
                    transaction.setDeleteFlag(deleteFlag);

                    transactionMap.put(transactionCode, transaction);
                } else {
                    Transaction transaction = transactionMap.get(transactionCode);
                    // add the equipment
                    String eqCode = row.getCell(1).getStringCellValue();
                    Boolean isReturned = row.getCell(8).getBooleanCellValue();
                    Equipment equipment = eqService.get(eqCode);

                    List<Equipment> equipmentsHist = transaction.getEquipmentsHist();
                    List<Equipment> equipments = transaction.getEquipments();
                    equipmentsHist.add(equipment);
                    // Add to equipments as well if its not returned yet
                    if (!isReturned && !equipment.getIsDuplicable()) {
                        equipments.add(equipment);
                    }
                    transaction.setEquipments(equipments);
                    transaction.setEquipmentsHist(equipmentsHist);
                    transactionMap.put(transaction.getTxCode(), transaction);
                }
            }

            List<Transaction> transactions = transactionMap.values().stream().toList();
            return transactions;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ApiException("Something went wrong when converting Excel file to Transactions", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public List<Transaction> getStudentTransactions(String studentNumber) {
        return txRepo.findByBorrowerStudentNumber(studentNumber);
    }

    public List<Transaction> getProfessorTransactions(String professorName) {
        return txRepo.findByProfessorName(professorName);
    }
}

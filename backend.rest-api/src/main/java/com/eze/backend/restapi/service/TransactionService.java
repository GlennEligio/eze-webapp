package com.eze.backend.restapi.service;

import com.eze.backend.restapi.dtos.StudentListDto;
import com.eze.backend.restapi.enums.TxStatus;
import com.eze.backend.restapi.exception.ApiException;
import com.eze.backend.restapi.model.*;
import com.eze.backend.restapi.repository.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final YearSectionService ysService;
    private final YearLevelService ylService;

    public TransactionService(TransactionRepository txRepo, EquipmentService eqService, ProfessorService profService, StudentService studentService, YearSectionService ysService, YearLevelService ylService) {
        this.txRepo = txRepo;
        this.eqService = eqService;
        this.profService = profService;
        this.studentService = studentService;
        this.ysService = ysService;
        this.ylService = ylService;
    }

    @Override
    public List<Transaction> getAll() {
        log.info("Fetching all transactions");
        return txRepo.findAll();
    }

    @Override
    public List<Transaction> getAllNotDeleted() {
        return null;
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
        List<Equipment> equipments = transaction.getEquipments().parallelStream()
                .map(eq -> eqService.get(eq.getEquipmentCode()))
                .toList();

        // Check if any equipments is not duplicable and is already borrowed
        checkEqAlreadyBorrowed(equipments);

        // Set the isBorrowed of non-duplicable equipment to true
        equipments.forEach(e -> {
            if (Boolean.FALSE.equals(e.getIsDuplicable())) {
                e.setIsBorrowed(true);
            }
            eqService.update(e, e.getEquipmentCode());
        });

        // Populate Equipment and Equipment history data of transaction
        transaction.setEquipments(new ArrayList<>(equipments));
        transaction.setEquipmentsHist(new ArrayList<>(equipments));

        // Populate Professor and Student data of transaction
        String profName = transaction.getProfessor().getName();
        transaction.setProfessor(profService.get(profName));

        String studentNumber = transaction.getBorrower().getStudentNumber();
        transaction.setBorrower(studentService.get(studentNumber));

        // Set the transaction code
        transaction.setTxCode(new ObjectId().toHexString());

        // Add borrowed at in case its null
        if (transaction.getBorrowedAt() == null) transaction.setBorrowedAt(LocalDateTime.now());

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
            transaction.setEquipments(eqs);
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
    public void delete(Serializable code) {
        log.info("Deleting transaction with {}", code);
        Transaction transaction = txRepo.findByTxCode(code.toString()).orElseThrow(() -> new ApiException(notFound(code), HttpStatus.NOT_FOUND));
        txRepo.delete(transaction);
    }

    @Override
    public void softDelete(Serializable id) {

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

    private String eqBorrowed(List<Equipment> equipments) {
        String equipmentIds = equipments.stream()
                .map(Equipment::getEquipmentCode)
                .reduce("", (ids, eqId) -> ids + ", " + eqId);
        return "The following equipments is already borrowed: " + equipmentIds;
    }

    private void checkEqAlreadyBorrowed(List<Equipment> equipments) {
        // Check if any equipments is not duplicable and is already borrowed
        List<Equipment> eqAlreadyBorrowed = equipments.parallelStream()
                .filter(eq -> !eq.getIsDuplicable())
                .filter(Equipment::getIsBorrowed)
                .toList();

        // If there is such equipment, list them and show them in response error
        if (!eqAlreadyBorrowed.isEmpty()) {
            throw new ApiException(eqBorrowed(eqAlreadyBorrowed), HttpStatus.BAD_REQUEST);
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
        Transaction transactionMatch = getAll().stream()
                .filter(t -> t.getBorrower().getStudentNumber().equalsIgnoreCase(borrower)
                        && t.getProfessor().getName().equalsIgnoreCase(professor)
                        && t.getReturnedAt() == null)
                .filter(t -> t.getEquipments().containsAll(eqs))
                .findFirst()
                .orElseThrow(() -> new ApiException(String.format("No transaction found that matches the following: Borrower %s, Professor %s, Barcodes %s", borrower, professor, equipmentsBarcode), HttpStatus.NOT_FOUND));

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
        if (newEqs.isEmpty()) transactionMatch.setReturnedAt(LocalDateTime.now());
        log.info("Update the transaction returnedAt value");
        log.info("New transaction {}", Transaction.toTransactionDto(transactionMatch));
        return txRepo.save(transactionMatch);
    }

    @Override
    public ByteArrayInputStream listToExcel(List<Transaction> transactions) {
        List<String> columnName = List.of("Transaction Code", "Equipment", "Borrower", "Year and Section", "Professor", "Borrowed At", "Returned At", "Status", "Is Returned");
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
                    if (!isReturned) {
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
                    transactionMap.put(transactionCode, transaction);
                } else {
                    Transaction transaction = transactionMap.get(transactionCode);

                    // add the equipment
                    String eqCode = row.getCell(1).getStringCellValue();
                    boolean isReturned = row.getCell(8).getBooleanCellValue();
                    Equipment equipment = eqService.get(eqCode);

                    List<Equipment> equipmentsHist = transaction.getEquipmentsHist();
                    List<Equipment> equipments = transaction.getEquipments();
                    equipmentsHist.add(equipment);
                    // Add to equipments as well if its not returned yet
                    if (!isReturned) equipments.add(equipment);
                    transaction.setEquipmentsHist(equipments);
                }
            }

            return transactionMap.values().stream().toList();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ApiException("Something went wrong when converting Excel file to Transactions", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

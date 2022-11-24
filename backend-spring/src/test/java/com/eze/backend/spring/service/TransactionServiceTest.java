package com.eze.backend.spring.service;

import static org.junit.jupiter.api.Assertions.*;

import com.eze.backend.spring.enums.EqStatus;
import com.eze.backend.spring.enums.TxStatus;
import com.eze.backend.spring.exception.ApiException;
import com.eze.backend.spring.model.*;
import com.eze.backend.spring.repository.TransactionRepository;
import com.eze.backend.spring.util.ObjectIdGenerator;
import com.eze.backend.spring.util.TimeStampProvider;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.LongStream;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class TransactionServiceTest {

    @Mock
    private TransactionRepository repository;
    @Mock
    private StudentService studentService;
    @Mock
    private ProfessorService professorService;
    @Mock
    private EquipmentService equipmentService;
    @Mock
    private TimeStampProvider timeStampProvider;
    @Mock
    private ObjectIdGenerator idGenerator;

    @InjectMocks
    private TransactionService service;

    private Student student1, student2;
    private YearLevel yearLevel;
    private YearSection yearSection;
    private Professor professor1, professor2;
    private Equipment eq0, eq1;
    private Transaction tx0, tx1, tx2;
    private String txCode0, txCode1, txCode2;
    private LocalDateTime timeStamp;
    private List<Transaction> transactionList;

    @BeforeEach
    void setup () {
        txCode0 = new ObjectId().toHexString();
        txCode1 = new ObjectId().toHexString();
        txCode2 = new ObjectId().toHexString();
        timeStamp = LocalDateTime.now();
        yearLevel = new YearLevel(1, "First", false);
        yearSection = new YearSection("SectionName1", false, yearLevel);
        student1 = new Student("2015-00129-MN-01", "FullName1", yearSection, "09062560571", "Birthday1", "Address1", "Email1", "Guardian1", "GuardianNumber1", yearLevel, "https://sampleprofile1.com", false);
        student2 = new Student("2015-00129-MN-02", "FullName2", yearSection, "09062560571", "Birthday2", "Address2", "Email2", "Guardian2", "GuardianNumber2", yearLevel, "https://sampleprofile2.com", false);
        professor1 = new Professor("Name1", "+639062560571", false);
        professor2 = new Professor("Name2", "+639062560572", false);
        eq0 = new Equipment("EqCode0", "Name0", "Barcode0", EqStatus.GOOD, LocalDateTime.now(), false, false, false);
        eq1 = new Equipment("EqCode1", "Name1", "Barcode1", EqStatus.GOOD, LocalDateTime.now(), true, false, false);

        tx0 = new Transaction(txCode0, List.of(eq0, eq1), List.of(eq0, eq1), student1, professor1, timeStamp, null, TxStatus.PENDING, false);
        tx1 = new Transaction(txCode1, List.of(eq0, eq1), List.of(eq0, eq1), student2, professor2, timeStamp, null, TxStatus.PENDING, false);
        tx2 = new Transaction(txCode2, List.of(eq0, eq1), List.of(eq0, eq1), student2, professor2, timeStamp, null, TxStatus.PENDING, true);
        transactionList = List.of(tx1, tx0, tx2);
    }

    @Test
    @DisplayName("Get all Transactions")
    void getAll_returnsTransactions() {
        Mockito.when(repository.findAll()).thenReturn(transactionList);

        List<Transaction> transactions = service.getAll();

        assertNotNull(transactions);
        assertEquals(transactionList, transactions);
    }

    @Test
    @DisplayName("Get all not deleted Transaction")
    void getAllNotDeleted_returnsNotDeleted() {
        List<Transaction> notDeleted = transactionList.stream().filter(t -> !t.getDeleteFlag()).toList();
        Mockito.when(repository.findAllNotDeleted()).thenReturn(notDeleted);

        List<Transaction> transactions = service.getAllNotDeleted();

        assertNotNull(transactions);
        assertEquals(0, transactions.stream().filter(Transaction::getDeleteFlag).count());
    }

    @Test
    @DisplayName("Get Transaction using valid Transaction code")
    void get_usingValidTransactionCode_returnsTransaction() {
        String validTxCode = tx0.getTxCode();
        Mockito.when(repository.findByTxCode(validTxCode)).thenReturn(Optional.of(tx0));

        Transaction transaction =service.get(validTxCode);

        assertNotNull(transaction);
        assertEquals(tx0, transaction);
    }

    @Test
    @DisplayName("Get Transaction using invalid Transaction code")
    void get_usingInvalidTransactionCode_throwsException() {
        String invalidTxCode = tx0.getTxCode();
        Mockito.when(repository.findByTxCode(invalidTxCode)).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> service.get(invalidTxCode));
    }

    @Test
    @DisplayName("Create Transaction with taken transaction code")
    void create_usingTakenTransactionCode_throwsException() {
        String takenTxCode = tx0.getTxCode();
        Mockito.when(repository.findByTxCode(takenTxCode)).thenReturn(Optional.of(tx0));

        assertThrows(ApiException.class, () -> service.create(tx0));
    }

    @Test
    @DisplayName("Create Transaction with available transaction code")
    void create_withNoTransactionCode_returnsNewTransaction() {
        String availableTxCode = tx0.getTxCode();
        // isBorrowed of not duplicable Equipments is changes to true
        Equipment borrowedEquipment0 = new Equipment(eq0.getEquipmentCode(), eq0.getName(), eq0.getBarcode(), eq0.getStatus(), eq0.getDefectiveSince(), false, true, false);
        Transaction newTransaction = new Transaction(txCode0, List.of(borrowedEquipment0), List.of(borrowedEquipment0, eq1), student1, professor1, timeStamp, null, TxStatus.PENDING, false);
        Mockito.when(idGenerator.createId()).thenReturn(txCode0);
        Mockito.when(repository.findByTxCode(availableTxCode)).thenReturn(Optional.empty());
        Mockito.when(equipmentService.get(eq0.getEquipmentCode())).thenReturn(eq0);
        Mockito.when(equipmentService.get(eq1.getEquipmentCode())).thenReturn(eq1);
        Mockito.when(professorService.get(professor1.getName())).thenReturn(professor1);
        Mockito.when(studentService.get(student1.getStudentNumber())).thenReturn(student1);
        log.info(newTransaction.toString());
        Mockito.when(repository.save(newTransaction)).thenReturn(newTransaction);

        Transaction transaction = service.create(tx0);

        assertNotNull(transaction);
        assertEquals(newTransaction, transaction);
        // All not duplicable equipments should have their
        assertEquals(0, transaction.getEquipments().stream().filter(e -> !e.getIsDuplicable())
                .filter(e -> !e.getIsBorrowed()).count());
    }

    @Test
    @DisplayName("Create Transaction where a non-duplicable Equipment is already borrowed")
    void create_withBorrowedNonDuplicableEquipment_throwsException() {
        String availableTxCode = tx0.getTxCode();
        // set a non duplicable equipment to a borrowed one
        eq0.setIsDuplicable(false);
        eq0.setIsBorrowed(true);
        tx0.setEquipments(List.of(eq0, eq1));
        Mockito.when(repository.findByTxCode(availableTxCode)).thenReturn(Optional.empty());
        Mockito.when(equipmentService.get(eq0.getEquipmentCode())).thenReturn(eq0);
        Mockito.when(equipmentService.get(eq1.getEquipmentCode())).thenReturn(eq1);

        assertThrows(ApiException.class, () -> service.create(tx0));
    }

    @Test
    @DisplayName("Update a non existent Transaction")
    void update_nonExistentTranscation_throwsException() {
        String invalidTxCode = tx0.getTxCode();
        Mockito.when(repository.findByTxCode(invalidTxCode)).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> service.update(tx0, invalidTxCode));
    }

    @Test
    @DisplayName("Update a existent Transaction with already borrowed Equipments")
    void update_existingTransactionWithAlreadyBorrowedEquipment_throwsException() {
        String validTxCode = tx0.getTxCode();
        // set a non duplicable equipment to a borrowed one
        eq0.setIsDuplicable(false);
        eq0.setIsBorrowed(true);
        tx0.setEquipments(List.of(eq0, eq1));
        tx0.setEquipmentsHist(List.of(eq0, eq1));
        Mockito.when(repository.findByTxCode(validTxCode)).thenReturn(Optional.of(tx0));
        Mockito.when(equipmentService.get(eq0.getEquipmentCode())).thenReturn(eq0);
        Mockito.when(equipmentService.get(eq1.getEquipmentCode())).thenReturn(eq1);

        assertThrows(ApiException.class, () -> service.update(tx0, validTxCode));
    }

    @Test
    @DisplayName("Update an existing Transaction with non-borrowed Equipments")
    void update_withExistingTransactionAndNonBorrowedEquipments_returnsUpdatedTransaction() {
        String validTxCode = tx0.getTxCode();
        Mockito.when(repository.findByTxCode(validTxCode)).thenReturn(Optional.of(tx0));
        Mockito.when(equipmentService.get(eq0.getEquipmentCode())).thenReturn(eq0);
        Mockito.when(equipmentService.get(eq1.getEquipmentCode())).thenReturn(eq1);
        Mockito.when(repository.save(tx0)).thenReturn(tx0);

        Transaction transaction = service.update(tx0, validTxCode);

        assertNotNull(transaction);
        assertEquals(tx0, transaction);
    }

    @Test
    @DisplayName("Delete non existing Transaction")
    void delete_nonExistentTransaction_throwsException() {
        String invalidTxCode = tx0.getTxCode();
        Mockito.when(repository.findByTxCode(invalidTxCode)).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> service.delete(invalidTxCode));
    }

    @Test
    @DisplayName("Delete existing Transaction")
    void delete_existingTransaction_doesNotThrowException() {
        String validTxCode = tx0.getTxCode();
        Mockito.when(repository.findByTxCode(validTxCode)).thenReturn(Optional.of(tx0));

        assertDoesNotThrow(() -> service.delete(validTxCode));
    }

    @Test
    @DisplayName("Soft delete non existent Transaction")
    void softDelete_nonExistentTransaction_throwsException() {
        String invalidTxCode = tx0.getTxCode();
        Mockito.when(repository.findByTxCode(invalidTxCode)).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> service.softDelete(invalidTxCode));
    }

    @Test
    @DisplayName("Soft delete existing and soft deleted Transaction")
    void softDelete_existingAndSoftDeletedTransaction_throwsException() {
        String validTxCode = tx0.getTxCode();
        tx0.setDeleteFlag(true);
        Mockito.when(repository.findByTxCode(validTxCode)).thenReturn(Optional.of(tx0));

        assertThrows(ApiException.class, () -> service.softDelete(validTxCode));
    }

    @Test
    @DisplayName("Soft delete existing and not yet soft deleted Transaction")
    void softDelete_existingAndNotYetSoftDeletedTransaction_doesNotThrowException() {
        String validTxCode = tx0.getTxCode();
        tx0.setDeleteFlag(false);
        Mockito.when(repository.findByTxCode(validTxCode)).thenReturn(Optional.of(tx0));

        assertDoesNotThrow(() -> service.softDelete(validTxCode));
    }

    @Test
    @DisplayName("Create a Not Found string")
    void notFound_createNotFoundString() {
        String code = txCode0;
        String expected = "No transaction with code " + code + " was found";

        String result = service.notFound(code);

        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Create an Already Exist string")
    void alreadyExist_createAlreadyExistString() {
        String code = txCode0;
        String expected = "Transaction with code " + code + " already exist";

        String result = service.alreadyExist(code);

        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Add or Update with same data and overwrite is false")
    void addOrUpdate_withSameDataAndOverwriteFalse_returnsZero() {
        List<Transaction> transactions = List.of(tx0, tx1);
        Mockito.when(repository.findByTxCode(tx0.getTxCode())).thenReturn(Optional.of(tx0));
        Mockito.when(repository.findByTxCode(tx1.getTxCode())).thenReturn(Optional.of(tx1));

        int itemsAffected = service.addOrUpdate(transactions, false);

        assertEquals(0, itemsAffected);
    }

    @Test
    @DisplayName("Add or Update with different data and overwrite is false")
    void addOrUpdate_withDifferentDataAndOverwriteFalse_returnsZero() {
        Transaction updatedTx0 = new Transaction(tx0.getTxCode(), tx0.getEquipments(), tx0.getEquipmentsHist(), student1, professor1, timeStamp, null, TxStatus.PENDING, !tx0.getDeleteFlag());
        List<Transaction> transactions = List.of(updatedTx0, tx1);
        Mockito.when(repository.findByTxCode(tx0.getTxCode())).thenReturn(Optional.of(tx0));
        Mockito.when(repository.findByTxCode(tx1.getTxCode())).thenReturn(Optional.of(tx1));

        int itemsAffected = service.addOrUpdate(transactions, false);

        assertEquals(0, itemsAffected);
    }

    @Test
    @DisplayName("Add or Update with different data and overwrite is true")
    void addOrUpdate_withDifferentDataAndOverwriteTrue_returnsZero() {
        Transaction updatedTx0 = new Transaction(tx0.getTxCode(), tx0.getEquipments(), tx0.getEquipmentsHist(), student1, professor1, timeStamp, null, TxStatus.PENDING, !tx0.getDeleteFlag());
        List<Transaction> transactions = List.of(updatedTx0, tx1);
        Mockito.when(repository.findByTxCode(tx0.getTxCode())).thenReturn(Optional.of(tx0));
        Mockito.when(repository.findByTxCode(tx1.getTxCode())).thenReturn(Optional.of(tx1));

        int itemsAffected = service.addOrUpdate(transactions, true);

        assertNotEquals(0, itemsAffected);
    }

    @Test
    @DisplayName("Checking equipments with an already borrowed equipment")
    void checkEqAlreadyBorrowed_withNonDuplicableBorrowedEquipment_doesNotThrowException() {
        List<Equipment> equipments = tx0.getEquipments();

        assertDoesNotThrow(() -> service.checkEqAlreadyBorrowed(equipments));
    }

    @Test
    @DisplayName("Checking equipments with no borrowed equipment")
    void checkEqAlreadyBorrowed_withNoNonDuplicableBorrowedEquipment_throwsException() {
        eq0.setIsDuplicable(false);
        eq0.setIsBorrowed(true);
        List<Equipment> equipment = List.of(eq0, eq1);

        assertThrows(ApiException.class, () -> service.checkEqAlreadyBorrowed(equipment));
    }

    @Test
    @DisplayName("Return Equipment where no transaction matches with given professor, borrower, and equipment barcodes")
    void returnEquipments_withNoTransactionsMatch_throwsException() {
        // make eq0 borrowed and non-duplicable
        List<String> validBarcodes = List.of(eq0.getBarcode(), eq1.getBarcode());
        String invalidProfessorName = "Invalid Professor Name";
        String invalidStudentName = "Invalid Student Name";
        Mockito.when(equipmentService.getByBarcode(eq0.getBarcode())).thenReturn(eq0);
        Mockito.when(equipmentService.getByBarcode(eq1.getBarcode())).thenReturn(eq1);

        assertThrows(ApiException.class, () -> service.returnEquipments(invalidStudentName, invalidProfessorName, validBarcodes));
    }

    @Test
    @DisplayName("Return Equipment where a transaction matches with given prof, student, and barcodes")
    void returnEquipments_withMatchingTransaction_updatesTransaction() {
        // make eq0 borrowed and non-duplicable
        Equipment returnedEq0 = new Equipment("EqCode0", "Name0", "Barcode0", EqStatus.GOOD, eq0.getDefectiveSince(), false, false, false);
        eq0.setIsBorrowed(true);
        eq0.setIsDuplicable(false);
        List<String> barcodes = List.of(eq0.getBarcode(), eq1.getBarcode());
        Transaction updatedTx0 = new Transaction(txCode0, new ArrayList<>(), List.of(returnedEq0, eq1), student1, professor1, timeStamp, timeStamp, TxStatus.PENDING, false);
        Mockito.when(equipmentService.getByBarcode(eq0.getBarcode())).thenReturn(eq0);
        Mockito.when(equipmentService.getByBarcode(eq1.getBarcode())).thenReturn(eq1);
        Mockito.when(timeStampProvider.getNow()).thenReturn(timeStamp);
        Mockito.when(repository.findAll()).thenReturn(transactionList);
        Mockito.when(repository.save(updatedTx0)).thenReturn(updatedTx0);

        Transaction transactionResult = service.returnEquipments(tx0.getBorrower().getStudentNumber(), tx0.getProfessor().getName(), barcodes);

        assertNotNull(transactionResult);
        assertEquals(updatedTx0, transactionResult);
    }

    @Test
    @DisplayName("Create Excel from a List of Transactions")
    void listToExcel_returnsExcelWithSameData() {
        try {
            List<Transaction> transactions = List.of(tx0, tx1);
            List<String> columns = List.of("Transaction Code", "Equipment", "Borrower", "Year and Section", "Professor", "Borrowed At", "Returned At", "Status", "Is Returned", "Delete flag");

            ByteArrayInputStream inputStream = service.listToExcel(transactions);
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheetAt(0);

            Row headerRow = sheet.getRow(0);
            for (int i = 0; i < columns.size(); i++) {
                String columnName = headerRow.getCell(i).getStringCellValue();
                assertEquals(columns.get(i), columnName);
            }

            int counter = 0;
            for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                Row row = sheet.getRow(i);
                Transaction transaction = transactions.get(counter);
                String txCode = row.getCell(0).getStringCellValue();
                // check if the row doesn't match the current transaction
                if(!transaction.getTxCode().equalsIgnoreCase(txCode)) {
                    counter++;
                    transaction = transactions.get(counter);
                }
                assertEquals(transaction.getTxCode(), txCode);

                String eqCode = row.getCell(1).getStringCellValue();
                Boolean eqIncludedInPending = row.getCell(8).getBooleanCellValue();
                assertNotEquals(0, transaction.getEquipmentsHist().stream().filter(e -> e.getEquipmentCode().equals(eqCode)).count());
                assertEquals(eqIncludedInPending, transaction.getEquipments().stream().anyMatch(e -> e.getEquipmentCode().equals(eqCode)));

                String studentNumber = row.getCell(2).getStringCellValue();
                assertEquals(transaction.getBorrower().getStudentNumber(), studentNumber);

                // get the professor of this transaction
                String professorName = row.getCell(4).getStringCellValue();
                assertEquals(transaction.getProfessor().getName(), professorName);

                // get the borrowed at info
                String borrowedAt = row.getCell(5).getStringCellValue();
                assertEquals(transaction.getBorrowedAt().toString(), borrowedAt);

                // get the returned at info if its present
                String returnedAt = row.getCell(6, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();
                if (returnedAt != null && !returnedAt.equals("")) {
                    assertEquals(transaction.getReturnedAt().toString(), returnedAt);
                }

                // get the status info if its present
                String status = row.getCell(7).getStringCellValue();
                assertEquals(transaction.getStatus().getName(), status);

                // get the isDeleteFlag
                Boolean deleteFlag = row.getCell(9).getBooleanCellValue();
                assertEquals(transaction.getDeleteFlag(), deleteFlag);

            }
        } catch (IOException ignored) {

        }
    }

    @Test
    @DisplayName("Create List of YearSection from Multipart file")
    void excelToList_returnsListOfAccount() {
        Mockito.when(equipmentService.get(eq0.getEquipmentCode())).thenReturn(eq0);
        Mockito.when(equipmentService.get(eq1.getEquipmentCode())).thenReturn(eq1);
        Mockito.when(studentService.get(student1.getStudentNumber())).thenReturn(student1);
        Mockito.when(studentService.get(student2.getStudentNumber())).thenReturn(student2);
        Mockito.when(professorService.get(professor1.getName())).thenReturn(professor1);
        Mockito.when(professorService.get(professor2.getName())).thenReturn(professor2);
        try {
            // remove duplicable eqs and returned equipments (isBorrowed is false)
            tx0.setEquipments(new ArrayList<>());
            tx0.setEquipmentsHist(List.of(eq0, eq1));
            tx1.setEquipments(new ArrayList<>());
            tx1.setEquipmentsHist(List.of(eq0, eq1));
            List<Transaction> transactionsExpected = List.of(tx0, tx1);
            XSSFWorkbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Transactions");

            List<String> columns = List.of("Transaction Code", "Equipment", "Borrower", "Year and Section", "Professor", "Borrowed At", "Returned At", "Status", "Is Returned", "Delete flag");

            // Creating header row
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columns.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns.get(i));
            }

            // Populating the Excel file with data
            int counter = 0;
            for (Transaction transaction : transactionsExpected) {
                // Create transaction data row per equipment, will cause redundancy
                for (int j = 0; j < transaction.getEquipmentsHist().size(); j++) {
                    Equipment equipmentHist = transaction.getEquipmentsHist().get(j);
                    Row dataRow = sheet.createRow(counter + 1);

                    dataRow.createCell(0).setCellValue(transaction.getTxCode());
                    dataRow.createCell(1).setCellValue(equipmentHist.getEquipmentCode());
                    dataRow.createCell(2).setCellValue(transaction.getBorrower().getStudentNumber());
                    dataRow.createCell(3).setCellValue(transaction.getBorrower().getYearAndSection().getSectionName());
                    dataRow.createCell(4).setCellValue(transaction.getProfessor().getName());
                    dataRow.createCell(5).setCellValue(transaction.getBorrowedAt().toString());
                    if (transaction.getReturnedAt() != null) {
                        dataRow.createCell(6).setCellValue(transaction.getReturnedAt().toString());
                    }
                    dataRow.createCell(7).setCellValue(transaction.getStatus().getName());

                    // Checks if the equipment is still in current Equipment list of Transaction
                    dataRow.createCell(8).setCellValue(!transaction.getEquipments().contains(equipmentHist));
                    dataRow.createCell(9).setCellValue(transaction.getDeleteFlag());
                    counter++;
                }
            }

            // Making size of the columns auto resize to fit data
            for (int i = 0; i < columns.size(); i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            MultipartFile file = new MockMultipartFile("file", new ByteArrayInputStream(outputStream.toByteArray()));

            List<Transaction> transactionsResult = service.excelToList(file);

            assertNotEquals(0, transactionsResult.size());

            // Sort the transactions first
            transactionsResult = transactionsResult.stream().sorted(Comparator.comparing(Transaction::getTxCode)).toList();
            transactionsExpected = transactionsExpected.stream().sorted(Comparator.comparing(Transaction::getTxCode)).toList();
            for (int i = 0; i < transactionsResult.size(); i++) {
                Transaction transactionExpected = transactionsExpected.get(i);
                Transaction transactionResult = transactionsResult.get(i);
                assertEquals(transactionExpected.getTxCode(), transactionResult.getTxCode());
                assertEquals(transactionExpected.getBorrower(), transactionResult.getBorrower());
                assertEquals(transactionExpected.getEquipments(), transactionResult.getEquipments());
                assertEquals(transactionExpected.getEquipmentsHist(), transactionResult.getEquipmentsHist());
                assertEquals(transactionExpected.getReturnedAt(), transactionResult.getReturnedAt());
                assertEquals(transactionExpected.getBorrowedAt(), transactionResult.getBorrowedAt());
                assertEquals(transactionExpected.getProfessor(), transactionResult.getProfessor());
                assertEquals(transactionExpected.getDeleteFlag(), transactionResult.getDeleteFlag());
                assertEquals(transactionExpected.getStatus(), transactionResult.getStatus());
                assertEquals(transactionExpected.getId(), transactionResult.getId());
            }
        } catch (IOException ignored) {

        }
    }

    @Test
    @DisplayName("Get all Transactions of the Student")
    void getStudentTransactions_returnsTransactionsOfStudents() {
        String studentNumber = student1.getStudentNumber();
        List<Transaction> notDeletedTransactions = transactionList.stream().filter(t -> !t.getDeleteFlag()).toList();
        List<Transaction> studentTransactions = notDeletedTransactions.stream().filter(t -> t.getBorrower().getStudentNumber().equalsIgnoreCase(studentNumber)).toList();
        Mockito.when(repository.findByBorrowerStudentNumber(studentNumber)).thenReturn(studentTransactions);

        List<Transaction> transactionsResult = service.getStudentTransactions(studentNumber);

        assertNotNull(transactionsResult);
        assertEquals(studentTransactions, transactionsResult);
        assertEquals(0, transactionsResult.stream().filter(t -> !t.getBorrower().getStudentNumber().equalsIgnoreCase(studentNumber)).count());
    }

    @Test
    @DisplayName("Get all Transactions of the Professor")
    void getProfessorTransactions_returnsTransactionsOfProfessor() {
        String professorName = professor1.getName();
        List<Transaction> notDeletedTransactions = transactionList.stream().filter(t -> !t.getDeleteFlag()).toList();
        List<Transaction> professorTransactions = notDeletedTransactions.stream().filter(t -> t.getProfessor().getName().equalsIgnoreCase(professorName)).toList();
        Mockito.when(repository.findByProfessorName(professorName)).thenReturn(professorTransactions);

        List<Transaction> transactionsResult = service.getProfessorTransactions(professorName);

        assertNotNull(transactionsResult);
        assertEquals(professorTransactions, transactionsResult);
        assertEquals(0, transactionsResult.stream().filter(t -> !t.getProfessor().getName().equalsIgnoreCase(professorName)).count());
    }
}

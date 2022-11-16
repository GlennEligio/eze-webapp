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
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    private Student student;
    private YearLevel yearLevel;
    private YearSection yearSection;
    private Professor professor;
    private Equipment eq0, eq1;
    private Transaction tx0, tx1;
    private String txCode0, txCode1;
    private LocalDateTime timeStamp;
    private List<Transaction> transactionList;

    @BeforeEach
    void setup () {
        txCode0 = new ObjectId().toHexString();
        txCode1 = new ObjectId().toHexString();
        timeStamp = LocalDateTime.now();
        yearLevel = new YearLevel(1, "First", false);
        yearSection = new YearSection("SectionName1", false, yearLevel);
        student = new Student("2015-00129-MN-01", "FullName1", yearSection, "09062560571", "Birthday1", "Address1", "Email1", "Guardian1", "GuardianNumber1", yearLevel, "https://sampleprofile1.com", true);
        professor = new Professor("Name1", "+639062560574", true);
        eq0 = new Equipment("EqCode0", "Name0", "Barcode0", EqStatus.GOOD, LocalDateTime.now(), false, false, false);
        eq1 = new Equipment("EqCode1", "Name1", "Barcode1", EqStatus.GOOD, LocalDateTime.now(), true, false, false);

        tx0 = new Transaction(txCode0, List.of(eq0, eq1), List.of(eq0, eq1), student, professor, timeStamp, null, TxStatus.PENDING, false);
        tx1 = new Transaction(txCode1, List.of(eq0, eq1), List.of(eq0, eq1), student, professor, timeStamp, null, TxStatus.PENDING, true);
        transactionList = List.of(tx1, tx0);
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
        Transaction newTransaction = new Transaction(txCode0, List.of(borrowedEquipment0), List.of(borrowedEquipment0, eq1), student, professor, timeStamp, null, TxStatus.PENDING, false);
        Mockito.when(idGenerator.createId()).thenReturn(txCode0);
        Mockito.when(repository.findByTxCode(availableTxCode)).thenReturn(Optional.empty());
        Mockito.when(equipmentService.get(eq0.getEquipmentCode())).thenReturn(eq0);
        Mockito.when(equipmentService.get(eq1.getEquipmentCode())).thenReturn(eq1);
        Mockito.when(professorService.get(professor.getName())).thenReturn(professor);
        Mockito.when(studentService.get(student.getStudentNumber())).thenReturn(student);
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
        Transaction updatedTx0 = new Transaction(tx0.getTxCode(), tx0.getEquipments(), tx0.getEquipmentsHist(), student, professor, timeStamp, null, TxStatus.PENDING, !tx0.getDeleteFlag());
        List<Transaction> transactions = List.of(updatedTx0, tx1);
        Mockito.when(repository.findByTxCode(tx0.getTxCode())).thenReturn(Optional.of(tx0));
        Mockito.when(repository.findByTxCode(tx1.getTxCode())).thenReturn(Optional.of(tx1));

        int itemsAffected = service.addOrUpdate(transactions, false);

        assertEquals(0, itemsAffected);
    }

    @Test
    @DisplayName("Add or Update with different data and overwrite is true")
    void addOrUpdate_withDifferentDataAndOverwriteTrue_returnsZero() {
        Transaction updatedTx0 = new Transaction(tx0.getTxCode(), tx0.getEquipments(), tx0.getEquipmentsHist(), student, professor, timeStamp, null, TxStatus.PENDING, !tx0.getDeleteFlag());
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
}

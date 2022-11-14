package com.eze.backend.spring.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.eze.backend.spring.enums.EqStatus;
import com.eze.backend.spring.enums.TxStatus;
import com.eze.backend.spring.model.*;
import com.eze.backend.spring.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@DataJpaTest
public class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private Transaction transaction0;

    @BeforeEach
    void setup() {
        YearLevel yearLevel = new YearLevel(1, "First", false);
        YearSection yearSection = new YearSection("SectionName1", false, yearLevel);
        Student student1 = new Student("2015-00129-MN-01", "FullName1", yearSection, "09062560571", "Birthday1", "Address1", "Email1", "Guardian1", "GuardianNumber1", yearLevel, "https://sampleprofile1.com", true);
        Professor professor1 = new Professor("Name1", "+639062560574", true);
        Equipment equipment0 = new Equipment("EqCode0", "Name0", "Barcode0", EqStatus.GOOD, LocalDateTime.now(), true, false, false);
        Equipment equipment1 = new Equipment("EqCode01", "Name1", "Barcode1", EqStatus.GOOD, LocalDateTime.now(), true, false, false);

        transaction0 = new Transaction("TxCode0", List.of(equipment0, equipment1), List.of(equipment0, equipment1), student1, professor1, LocalDateTime.now(), null, TxStatus.PENDING, false);
        Transaction transaction1 = new Transaction("TxCode1", List.of(equipment0, equipment1), List.of(equipment0, equipment1), student1, professor1, LocalDateTime.now(), null, TxStatus.PENDING, true);

        entityManager.persist(yearLevel);
        entityManager.persist(yearSection);
        entityManager.persist(student1);
        entityManager.persist(professor1);
        entityManager.persist(equipment0);
        entityManager.persist(equipment0);
        entityManager.persist(transaction1);
        entityManager.persist(transaction0);
    }

    @Test
    @DisplayName("Find Transaction using valid Transaction Code")
    void findByTxCode_usingValidTxCode_returnsTransaction() {
        String validTxCode = "TxCode0";

        Optional<Transaction> transactionOptional = repository.findByTxCode(validTxCode);

        assertTrue(transactionOptional.isPresent());
        assertEquals(transactionOptional.get(), transaction0);
    }

    @Test
    @DisplayName("Find Transaction using invalid Transaction Code")
    void findByTxCode_usingInvalidTxCode_returnsEmpty() {
        String invalidTxCode = "InvalidTxCode";

        Optional<Transaction> transactionOptional = repository.findByTxCode(invalidTxCode);

        assertTrue(transactionOptional.isEmpty());
    }

    @Test
    @DisplayName("Find All Not Deleted Transactions")
    void findAllNotDeleted_returnsNotDeletedTransactions() {
        List<Transaction> transactionList = repository.findAllNotDeleted();

        assertEquals(0, transactionList.stream().filter(Transaction::getDeleteFlag).count());
    }

    @Test
    @DisplayName("Soft deletes a Transaction")
    void softDelete_updatesTransactionDeleteFlag() {
        String validTxCode = "TxCode0";

        repository.softDelete(validTxCode);
        Optional<Transaction> transactionOptional = repository.findByTxCode(validTxCode);

        assertTrue(transactionOptional.isPresent());
        assertTrue(transactionOptional.get().getDeleteFlag());
    }
}

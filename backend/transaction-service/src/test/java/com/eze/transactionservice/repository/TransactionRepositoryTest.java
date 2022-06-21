package com.eze.transactionservice.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.eze.transactionservice.domain.Item;
import com.eze.transactionservice.domain.Status;
import com.eze.transactionservice.domain.Transaction;
import com.eze.transactionservice.domain.TransactionItem;
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
class TransactionRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private TransactionRepository transactionRepository;

    private Transaction transaction0;

    @BeforeEach
    void setup() {
        Item item0 = new Item("itemCode0");
        Item item1 = new Item("itemCode1");
        TransactionItem transItem0 = new TransactionItem(1L, item0);
        TransactionItem transItem1 = new TransactionItem(2L, item1);
        transaction0 = new Transaction("transactionId0",
                List.of(transItem1, transItem0),
                "acceptor0",
                "requester0",
                Status.PENDING,
                LocalDateTime.now(),
                LocalDateTime.now(),
                false);
        Transaction transaction1 = new Transaction("transactionId1",
                List.of(transItem1, transItem0),
                "acceptor1",
                "requester1",
                Status.DENIED,
                LocalDateTime.now(),
                LocalDateTime.now(),
                false);
        Transaction transaction2 = new Transaction("transactionId2",
                List.of(transItem1, transItem0),
                "acceptor2",
                "requester2",
                Status.ACCEPTED,
                LocalDateTime.now(),
                LocalDateTime.now(),
                false);
        List.of(transaction1, transaction0, transaction2).forEach(transaction -> testEntityManager.persist(transaction));
    }

    @DisplayName("find Transactions with deleteFlag set to false, returns only Transactions with deleteFlag false")
    @Test
    void findByDeleteFlagFalse_withItemsPresent_returnsOnlyItemsWithDeleteFlagFalse() {
        List<Transaction> transactions = transactionRepository.findByDeleteFlagFalse();

        assertEquals(0, transactions.stream().filter(transaction -> transaction.getDeleteFlag().equals(true)).count());
    }

    @DisplayName("find Transaction with valid TransactionId and returns Transaction")
    @Test
    void findByTransactionIdAndDeleteFlagFalse_withValidTransactionId_returnTransaction() {
        String validTransactionId = transaction0.getTransactionCode();
        Optional<Transaction> transactionOp = transactionRepository.findByTransactionCodeAndDeleteFlagFalse(validTransactionId);

        assertTrue(transactionOp.isPresent());
    }

    @DisplayName("find Transaction with invalid TransactionId and returns no Transaction")
    @Test
    void findByTransactionIdAndDeleteFlagFalse_withInvalidTransactionId_returnsNoTransaction() {
        String invalidTransactionId = "someInvalidId";
        Optional<Transaction> transactionOp = transactionRepository.findByTransactionCodeAndDeleteFlagFalse(invalidTransactionId);

        assertTrue(transactionOp.isEmpty());
    }

    @DisplayName("soft deletes with valid TransactionId and changes the deleteFlag of the Transaction")
    @Test
    void softDelete_withValidTransactionId_changesTransactionDeleteFlag() {
        transactionRepository.softDelete(transaction0.getTransactionCode());

        Optional<Transaction> transactionOp = transactionRepository.findByTransactionCodeAndDeleteFlagFalse(transaction0.getTransactionCode());
        assertTrue(transactionOp.isEmpty());
    }

    @DisplayName("fetch transaction using specific requester id value and status and returns only Transactions with same requestedBy and status")
    @Test
    void findTransactionsByRequestedBy_usingValidRequestByAndStatus_returnsTransactionsWithCorrectRequestedByAndStatus() {
        String validRequestedBy = "requester0";
        Status validStatus = Status.PENDING;

        List<Transaction> transactions = transactionRepository.findTransactionByRequestedBy(validRequestedBy, validStatus);

        assertEquals(0, transactions.stream().filter(t -> !t.getStatus().equals(validStatus) || !t.getRequestedBy().equals(validRequestedBy)).count());
    }

    @DisplayName("fetch transaction using specific acceptor id value and status and returns only Transactions with same acceptedBy and status")
    @Test
    void findTransactionsByAcceptedBy_usingValidAcceptedByAndStatus_returnsTransactionsWithCorrectAcceptedByAndStatus() {
        String validAcceptedBy = "acceptor1";
        Status validStatus = Status.PENDING;

        List<Transaction> transactions = transactionRepository.findTransactionByRequestedBy(validAcceptedBy, validStatus);

        assertEquals(0, transactions.stream().filter(t -> !t.getStatus().equals(validStatus) || !t.getAcceptedBy().equals(validAcceptedBy)).count());
    }
}

package com.eze.transactionservice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.eze.transactionservice.domain.Item;
import com.eze.transactionservice.domain.Status;
import com.eze.transactionservice.domain.Transaction;
import com.eze.transactionservice.domain.TransactionItem;
import com.eze.transactionservice.exception.ApiException;
import com.eze.transactionservice.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@SpringBootTest
class TransactionServiceTest {

    @MockBean
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionService transactionService;

    private Transaction transaction0;
    private List<Transaction> transactions;

    @BeforeEach
    void setup() {
        Item item0 = new Item("itemCode0");
        Item item1 = new Item("itemCode1");
        TransactionItem transItem0 = new TransactionItem(1L, item0);
        TransactionItem transItem1 = new TransactionItem(2L, item1);
        transaction0 = new Transaction("transactionId0",
                List.of(transItem1, transItem0),
                "accepter0",
                "requester0",
                Status.PENDING,
                LocalDateTime.now(),
                LocalDateTime.now(),
                false);
        Transaction transaction1 = new Transaction("transactionId1",
                List.of(transItem1, transItem0),
                "accepter1",
                "requester1",
                Status.PENDING,
                LocalDateTime.now(),
                LocalDateTime.now(),
                false);
        Transaction transaction2 = new Transaction("transactionId2",
                List.of(transItem1, transItem0),
                "accepter2",
                "requester2",
                Status.PENDING,
                LocalDateTime.now(),
                LocalDateTime.now(),
                false);
        transactions = List.of(transaction0, transaction1, transaction2);
    }

    @DisplayName("find Transactions with deleteFlag set to false")
    @Test
    void findAllTransactions_withTransactionsPresent_returnOnlyTransactionsWithDeleteFlagFalse() {
        when(transactionRepository.findByDeleteFlagFalse()).thenReturn(transactions);

        assertEquals(0, transactionService.findAllTransactions().stream().filter(tx -> tx.getDeleteFlag().equals(true)).count());
    }

    @DisplayName("find Transaction with valid TransactionId and return Transaction")
    @Test
    void findTransaction_withValidTransactionId_returnsTransaction() {
        String validTxId = transaction0.getTransactionId();
        when(transactionRepository.findByTransactionIdAndDeleteFlagFalse(validTxId)).thenReturn(Optional.of(transaction0));

        assertDoesNotThrow(() -> transactionService.findTransaction(validTxId));
        assertNotNull(transactionService.findTransaction(validTxId));
    }

    @DisplayName("find Transaction with invalid TransactionId and throws exception")
    @Test
    void findTransaction_withInvalidTransactionId_throwsException() {
        String invalidTxId = "invalidTxId";
        when(transactionRepository.findByTransactionIdAndDeleteFlagFalse(invalidTxId)).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> transactionService.findTransaction(invalidTxId));
    }

    @DisplayName("create new Transaction and return the Transaction")
    @Test
    void createTransaction_withNewTransaction_returnTransaction() {
        String validTxId = transaction0.getTransactionId();
        when(transactionRepository.findByTransactionIdAndDeleteFlagFalse(validTxId)).thenReturn(Optional.empty());
        when(transactionRepository.save(transaction0)).thenReturn(transaction0);

        assertDoesNotThrow(() -> transactionService.createTransaction(transaction0));
        assertNotNull(transactionService.createTransaction(transaction0));
    }

    @DisplayName("create existing Transaction and throws exception")
    @Test
    void createTransaction_withExistingTransaction_throwsException() {
        String invalidTxId = transaction0.getTransactionId();
        when(transactionRepository.findByTransactionIdAndDeleteFlagFalse(invalidTxId)).thenReturn(Optional.of(transaction0));

        assertThrows(ApiException.class, () -> transactionService.createTransaction(transaction0));
    }

    // TODO: Test setter functions effect in Unit Test
    @DisplayName("update existing Transaction and returns updated Transaction")
    @Test
    void updateTransaction_withExistingTransaction_returnsUpdatedTransaction() {
        String validTxId = transaction0.getTransactionId();
        when(transactionRepository.findByTransactionIdAndDeleteFlagFalse(validTxId)).thenReturn(Optional.of(transaction0));
        when(transactionRepository.save(transaction0)).thenReturn(transaction0);

        assertNotNull(transactionService.updateTransaction(transaction0));
    }

    @DisplayName("updated non-existing Transaction and throws exception")
    @Test
    void updateTransaction_withNonExistingTransaction_throwsException() {
        String invalidTxId = transaction0.getTransactionId();
        when(transactionRepository.findByTransactionIdAndDeleteFlagFalse(invalidTxId)).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> transactionService.updateTransaction(transaction0));
    }

    @DisplayName("delete existing Transaction and returns true")
    @Test
    void deleteTransaction_withValidTransactionId_returnsTrue() {
        String validTxId = transaction0.getTransactionId();
        when(transactionRepository.findByTransactionIdAndDeleteFlagFalse(validTxId)).thenReturn(Optional.of(transaction0));

        assertTrue(transactionService.deleteTransaction(validTxId));
    }

    @DisplayName("delete non-existing Transaction and throwsException")
    @Test
    void deleteTransaction_withInvalidTransactionId_throwsException() {
        String invalidTxId = transaction0.getTransactionId();
        when(transactionRepository.findByTransactionIdAndDeleteFlagFalse(invalidTxId)).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> transactionService.deleteTransaction(invalidTxId));
    }
}

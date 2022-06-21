package com.eze.transactionservice.service;

import com.eze.transactionservice.domain.Transaction;

import java.util.List;

public interface TransactionService {
    List<Transaction> findAllTransactions();
    Transaction findTransaction(String transactionCode);
    Transaction createTransaction (Transaction transaction);
    Transaction updateTransaction (Transaction transaction);
    Boolean deleteTransaction (String transactionCode);
    List<Transaction> findProfessorTransactions(String profId, String status);
    List<Transaction> findStudentTransactions(String studentId, String status);
}

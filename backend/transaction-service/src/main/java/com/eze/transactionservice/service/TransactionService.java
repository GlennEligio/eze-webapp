package com.eze.transactionservice.service;

import com.eze.transactionservice.domain.Transaction;

import java.util.List;

public interface TransactionService {
    List<Transaction> findAllTransactions();
    Transaction findTransaction(String transactionId);
    Transaction createTransaction (Transaction transaction);
    void updateTransaction (Transaction transaction);
    void deleteTransaction (String transactionId);
}

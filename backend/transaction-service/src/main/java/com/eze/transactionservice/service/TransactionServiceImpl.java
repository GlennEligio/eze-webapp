package com.eze.transactionservice.service;

import com.eze.transactionservice.domain.Transaction;
import com.eze.transactionservice.exception.ApiException;
import com.eze.transactionservice.repository.TransactionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionServiceImpl implements TransactionService{

    private TransactionRepository repository;

    public TransactionServiceImpl(TransactionRepository repository){
        this.repository = repository;
    }

    @Override
    public List<Transaction> findAllTransactions() {
        return repository.findAll();
    }

    @Override
    public Transaction findTransaction(String transactionId) {
        Optional<Transaction> transactionOp = repository.findByTransactionIdAndDeleteFlagFalse(transactionId);
        return transactionOp.orElseThrow(() -> new ApiException("No transaction with transaction id " + transactionId + " was found", HttpStatus.NOT_FOUND));
    }

    @Override
    public Transaction createTransaction(Transaction transaction) {
        Optional<Transaction> transactionOp = repository.findByTransactionIdAndDeleteFlagFalse(transaction.getTransactionId());
        if (transactionOp.isPresent()){
            throw new ApiException("Transaction with transaction id " + transaction.getTransactionId() + " already exist", HttpStatus.BAD_REQUEST);
        }
        return repository.save(transaction);
    }

    @Override
    public void updateTransaction(Transaction transaction) {
        Optional<Transaction> transactionOp = repository.findByTransactionIdAndDeleteFlagFalse(transaction.getTransactionId());
        Transaction updatedTransaction = transactionOp.orElseThrow(() -> new ApiException("No transaction with transaction id " + transaction.getTransactionId() + " was found", HttpStatus.NOT_FOUND));
        updatedTransaction.setTransactionItems(transaction.getTransactionItems());
        updatedTransaction.setAcceptedBy(transaction.getAcceptedBy());
        updatedTransaction.setRequestedBy(transaction.getRequestedBy());
        updatedTransaction.setDateCreated(transaction.getDateCreated());
        updatedTransaction.setDateResolved(transaction.getDateResolved());
        updatedTransaction.setStatus(transaction.getStatus());
        repository.save(updatedTransaction);
    }

    @Transactional
    @Override
    public void deleteTransaction(String transactionId) {
        Optional<Transaction> transactionOp = repository.findByTransactionIdAndDeleteFlagFalse(transactionId);
        repository.softDelete(transactionOp.orElseThrow(() -> new ApiException("No transaction with transaction id " + transactionId + " was found", HttpStatus.NOT_FOUND)).getTransactionId());
    }
}

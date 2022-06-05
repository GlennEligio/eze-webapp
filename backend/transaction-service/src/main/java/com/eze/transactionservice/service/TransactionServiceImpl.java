package com.eze.transactionservice.service;

import com.eze.transactionservice.domain.Status;
import com.eze.transactionservice.domain.Transaction;
import com.eze.transactionservice.exception.ApiException;
import com.eze.transactionservice.repository.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@Slf4j
public class TransactionServiceImpl implements TransactionService{

    private final TransactionRepository repository;

    public TransactionServiceImpl(TransactionRepository repository){
        this.repository = repository;
    }

    @Override
    public List<Transaction> findAllTransactions() {
        return repository.findByDeleteFlagFalse();
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
        transaction.setDeleteFlag(false);
        transaction.setDateCreated(LocalDateTime.now());
        return repository.save(transaction);
    }

    @Override
    public Transaction updateTransaction(Transaction transaction) {
        Optional<Transaction> transactionOp = repository.findByTransactionIdAndDeleteFlagFalse(transaction.getTransactionId());
        Transaction updatedTransaction = transactionOp.orElseThrow(() -> new ApiException("No transaction with transaction id " + transaction.getTransactionId() + " was found", HttpStatus.NOT_FOUND));
        updatedTransaction.setTransactionItems(transaction.getTransactionItems());
        updatedTransaction.setAcceptedBy(transaction.getAcceptedBy());
        updatedTransaction.setRequestedBy(transaction.getRequestedBy());
        updatedTransaction.setDateCreated(transaction.getDateCreated());
        updatedTransaction.setDateResolved(transaction.getDateResolved());
        updatedTransaction.setStatus(transaction.getStatus());
        return repository.save(updatedTransaction);
    }

    @Transactional
    @Override
    public Boolean deleteTransaction(String transactionId) {
        Optional<Transaction> transactionOp = repository.findByTransactionIdAndDeleteFlagFalse(transactionId);
        repository.softDelete(transactionOp.orElseThrow(() -> new ApiException("No transaction with transaction id " + transactionId + " was found", HttpStatus.NOT_FOUND)).getTransactionId());
        return true;
    }

    @Override
    public List<Transaction> findProfessorTransactions(String profId, String status) {
        if(status.equals(Status.DENIED.getStatusName())
                || status.equals(Status.ACCEPTED.getStatusName())
                || status.equals(Status.PENDING.getStatusName())){return repository.findTransactionByAcceptedBy(profId, Status.valueOf(status.toUpperCase(Locale.ROOT)));
        }
        throw new ApiException("Wrong status name used, can only use [pending, denied, accepted]", HttpStatus.BAD_REQUEST);
    }

    @Override
    public List<Transaction> findStudentTransactions(String studentId, String status) {
        if(status.equals(Status.DENIED.getStatusName())
                || status.equals(Status.ACCEPTED.getStatusName())
                || status.equals(Status.PENDING.getStatusName())){return repository.findTransactionByRequestedBy(studentId, Status.valueOf(status.toUpperCase(Locale.ROOT)));
        }
        throw new ApiException("Wrong status name used, can only use [pending, denied, accepted]", HttpStatus.BAD_REQUEST);
    }
}

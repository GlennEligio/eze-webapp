package com.eze.transactionservice.service;

import com.eze.transactionservice.domain.Status;
import com.eze.transactionservice.domain.Transaction;
import com.eze.transactionservice.domain.TransactionItem;
import com.eze.transactionservice.exception.ApiException;
import com.eze.transactionservice.repository.TransactionItemRepository;
import com.eze.transactionservice.repository.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TransactionServiceImpl implements TransactionService{

    private final TransactionRepository transactionRepository;
    private final TransactionItemRepository transactionItemRepository;

    public TransactionServiceImpl(TransactionRepository transactionRepository, TransactionItemRepository transactionItemRepository) {
        this.transactionRepository = transactionRepository;
        this.transactionItemRepository = transactionItemRepository;
    }

    @Override
    public List<Transaction> findAllTransactions() {
        return transactionRepository.findByDeleteFlagFalse();
    }

    @Override
    public Transaction findTransaction(String transactionId) {
        Optional<Transaction> transactionOp = transactionRepository.findByTransactionCodeAndDeleteFlagFalse(transactionId);
        return transactionOp.orElseThrow(() -> new ApiException("No transaction with transaction id " + transactionId + " was found", HttpStatus.NOT_FOUND));
    }

    @Override
    public Transaction createTransaction(Transaction transaction) {
        Optional<Transaction> transactionOp = transactionRepository.findByTransactionCodeAndDeleteFlagFalse(transaction.getTransactionCode());
        if (transactionOp.isPresent()){
            throw new ApiException("Transaction with transaction id " + transaction.getTransactionCode() + " already exist", HttpStatus.BAD_REQUEST);
        }
        transaction.setDeleteFlag(false);
        transaction.setDateCreated(LocalDateTime.now());
        transaction.setTransactionCode(ObjectId.get().toHexString());

        transaction.setTransactionItems(transaction.getTransactionItems().stream().map(transactionItem -> {
            transactionItem.setTransactionItemCode(ObjectId.get().toHexString());
            return transactionItem;
        }).collect(Collectors.toList()));

        return transactionRepository.save(transaction);
    }

    // TODO: Update Unit Test where the returned all Transaction's TransactionItems must have transactionItemCode
    @Transactional
    @Override
    public Transaction updateTransaction(Transaction transaction) {
        Optional<Transaction> transactionOp = transactionRepository.findByTransactionCodeAndDeleteFlagFalse(transaction.getTransactionCode());
        Transaction updatedTransaction = transactionOp.orElseThrow(() -> new ApiException("No transaction with transaction id " + transaction.getTransactionCode() + " was found", HttpStatus.NOT_FOUND));
        updatedTransaction.setAcceptedBy(transaction.getAcceptedBy());
        updatedTransaction.setRequestedBy(transaction.getRequestedBy());
        updatedTransaction.setDateCreated(transaction.getDateCreated());
        updatedTransaction.setDateResolved(transaction.getDateResolved());
        updatedTransaction.setStatus(transaction.getStatus());

        updatedTransaction.setTransactionItems(transaction.getTransactionItems().stream().map(transactionItem -> {
            if(transactionItem.getTransactionItemCode() != null) {
                Optional<TransactionItem> txItemOp = transactionItemRepository.findByTransactionItemCode(transactionItem.getTransactionItemCode());
                txItemOp.ifPresent(item -> transactionItemRepository.deleteById(item.getId()));
            } else {
                transactionItem.setTransactionItemCode(ObjectId.get().toHexString());
            }
            return transactionItem;
        }).collect(Collectors.toList()));

        return transactionRepository.save(updatedTransaction);
    }

    @Transactional
    @Override
    public Boolean deleteTransaction(String transactionId) {
        Optional<Transaction> transactionOp = transactionRepository.findByTransactionCodeAndDeleteFlagFalse(transactionId);
        transactionRepository.softDelete(transactionOp.orElseThrow(() -> new ApiException("No transaction with transaction id " + transactionId + " was found", HttpStatus.NOT_FOUND)).getTransactionCode());
        return true;
    }

    @Override
    public List<Transaction> findProfessorTransactions(String profId, String status) {
        if(status.equals(Status.DENIED.getStatusName())
                || status.equals(Status.ACCEPTED.getStatusName())
                || status.equals(Status.PENDING.getStatusName())){return transactionRepository.findTransactionByAcceptedBy(profId, Status.valueOf(status.toUpperCase(Locale.ROOT)));
        }
        throw new ApiException("Wrong status name used, can only use [pending, denied, accepted]", HttpStatus.BAD_REQUEST);
    }

    @Override
    public List<Transaction> findStudentTransactions(String studentId, String status) {
        if(status.equals(Status.DENIED.getStatusName())
                || status.equals(Status.ACCEPTED.getStatusName())
                || status.equals(Status.PENDING.getStatusName())){return transactionRepository.findTransactionByRequestedBy(studentId, Status.valueOf(status.toUpperCase(Locale.ROOT)));
        }
        throw new ApiException("Wrong status name used, can only use [pending, denied, accepted]", HttpStatus.BAD_REQUEST);
    }
}

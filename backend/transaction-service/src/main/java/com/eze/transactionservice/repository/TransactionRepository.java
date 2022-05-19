package com.eze.transactionservice.repository;

import com.eze.transactionservice.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByDeleteFlagFalse();

    Optional<Transaction> findByTransactionIdAndDeleteFlagFalse(String transactionId);

    @Query("SELECT t FROM Transaction t " +
            "WHERE t.transactionId=?1 " +
            "AND t.deleteFlag=false")
    List<Transaction> findByTransactionId(String transactionId);

    @Query("UPDATE Transaction t SET t.deleteFlag=true WHERE t.transactionId=?1")
    @Modifying
    void softDelete(String transactionId);
}

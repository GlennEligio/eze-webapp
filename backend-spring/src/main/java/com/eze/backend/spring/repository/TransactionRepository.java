package com.eze.backend.spring.repository;

import com.eze.backend.spring.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByTxCode(String code);

    @Query( "SELECT t FROM Transaction t WHERE t.deleteFlag=false")
    List<Transaction> findAllNotDeleted();

    //Soft delete.
    @Query("UPDATE Transaction t SET t.deleteFlag=true WHERE t.txCode=?1")
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    void softDelete(String txCode);
}

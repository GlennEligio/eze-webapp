package com.eze.backend.restapi.repository;

import com.eze.backend.restapi.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByTxCode(String code);
}

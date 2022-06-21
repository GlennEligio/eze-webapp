package com.eze.transactionservice.repository;

import com.eze.transactionservice.domain.TransactionItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransactionItemRepository extends JpaRepository<TransactionItem, Long> {
    Optional<TransactionItem> findByTransactionItemCode(String transactionItemCode);
}

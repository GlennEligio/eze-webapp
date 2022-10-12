package com.eze.backend.restapi.repository;

import com.eze.backend.restapi.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByAccountCode(String code);
    Optional<Account> findByUsername(String username);
}

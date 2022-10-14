package com.eze.backend.restapi.repository;

import com.eze.backend.restapi.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByUsername(String username);
//    @Query("SELECT a FROM Account a WHERE a.username LIKE CONCAT('%', :username, '%')")
//    Account findAccountsByUsername(String username);
}

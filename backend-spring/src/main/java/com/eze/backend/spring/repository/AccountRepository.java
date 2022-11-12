package com.eze.backend.spring.repository;

import com.eze.backend.spring.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByUsername(String username);
//    @Query("SELECT a FROM Account a WHERE a.username LIKE CONCAT('%', :username, '%')")
//    Account findAccountsByUsername(String username);

    @Query( "SELECT a FROM Account a WHERE a.deleteFlag=false")
    List<Account> findAllNotDeleted();

    //Soft delete.
    @Query("UPDATE Account a SET a.deleteFlag=true WHERE a.username=?1")
    @Modifying
    void softDelete(String username);
}

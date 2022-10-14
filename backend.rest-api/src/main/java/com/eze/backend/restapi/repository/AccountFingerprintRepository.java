package com.eze.backend.restapi.repository;

import com.eze.backend.restapi.model.AccountFingerprint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AccountFingerprintRepository extends JpaRepository<AccountFingerprint, Long> {

    @Query("SELECT fp FROM AccountFingerprint fp " +
            "LEFT JOIN fp.account ac " +
            "WHERE ac.username=:username")
    Optional<AccountFingerprint> findByAccountUsername(String username);
}


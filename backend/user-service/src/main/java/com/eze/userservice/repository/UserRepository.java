package com.eze.userservice.repository;

import com.eze.userservice.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u " +
            "WHERE u.username=?1 " +
            "AND u.deleteFlag=false")
    List<User> findByUsername(String username);

    List<User> findByDeleteFlagFalse();

    @Query("UPDATE User u SET u.deleteFlag=true WHERE u.username=?1")
    @Modifying
    void softDelete(String username);
}

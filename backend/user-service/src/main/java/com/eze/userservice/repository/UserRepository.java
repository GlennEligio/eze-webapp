package com.eze.userservice.repository;

import com.eze.userservice.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsernameAndDeleteFlagFalse(String username);

    List<User> findByDeleteFlagFalse();

    @Query("UPDATE User u SET u.deleteFlag=true WHERE u.username=?1")
    @Modifying
    void softDelete(String username);
}

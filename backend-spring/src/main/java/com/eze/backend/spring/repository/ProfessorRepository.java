package com.eze.backend.spring.repository;

import com.eze.backend.spring.model.Professor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProfessorRepository extends JpaRepository<Professor, Long> {

    Optional<Professor> findByName(String name);

    @Query( "SELECT p FROM Professor p WHERE p.deleteFlag=false")
    List<Professor> findAllNotDeleted();

    //Soft delete.
    @Query("UPDATE Professor p SET p.deleteFlag=true WHERE p.name=?1")
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    void softDelete(String name);
}

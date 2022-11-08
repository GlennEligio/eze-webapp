package com.eze.backend.restapi.repository;

import com.eze.backend.restapi.model.Equipment;
import com.eze.backend.restapi.model.Professor;
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
    @Modifying
    void softDelete(String name);
}

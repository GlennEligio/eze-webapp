package com.eze.backend.restapi.repository;

import com.eze.backend.restapi.model.Professor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfessorRepository extends JpaRepository<Professor, Long> {

    Optional<Professor> findByName(String name);
}

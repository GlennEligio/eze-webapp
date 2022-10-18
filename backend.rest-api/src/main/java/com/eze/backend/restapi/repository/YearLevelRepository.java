package com.eze.backend.restapi.repository;

import com.eze.backend.restapi.model.YearLevel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface YearLevelRepository extends JpaRepository<YearLevel, Long> {
    Optional<YearLevel> findByYearNumber(Integer yearNumber);
}

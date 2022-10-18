package com.eze.backend.restapi.repository;

import com.eze.backend.restapi.model.YearSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface YearSectionRepository extends JpaRepository<YearSection, Long> {

    @Query("SELECT ys FROM YearSection ys " +
            "LEFT JOIN ys.yearLevel yl " +
            "WHERE yl.yearNumber=?1")
    List<YearSection> findByYearLevel(Integer yearNumber);
    Optional<YearSection> findBySectionName(String sectionName);
}

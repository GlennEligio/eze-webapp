package com.eze.backend.spring.repository;

import com.eze.backend.spring.model.YearLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface YearLevelRepository extends JpaRepository<YearLevel, Long> {
    Optional<YearLevel> findByYearNumber(Integer yearNumber);

    @Query( "SELECT yl FROM YearLevel yl WHERE yl.deleteFlag=false")
    List<YearLevel> findAllNotDeleted();

    //Soft delete.
    @Query("UPDATE YearLevel yl SET yl.deleteFlag=true WHERE yl.yearNumber=?1")
    @Modifying
    void softDelete(Integer yearNumber);
}

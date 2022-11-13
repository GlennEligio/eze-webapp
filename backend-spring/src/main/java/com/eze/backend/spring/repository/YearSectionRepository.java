package com.eze.backend.spring.repository;

import com.eze.backend.spring.model.YearSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface YearSectionRepository extends JpaRepository<YearSection, Long> {

    Optional<YearSection> findBySectionName(String sectionName);

    @Query( "SELECT ys FROM YearSection ys WHERE ys.deleteFlag=false")
    List<YearSection> findAllNotDeleted();

    //Soft delete.
    @Query("UPDATE YearSection ys SET ys.deleteFlag=true WHERE ys.sectionName=?1")
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    void softDelete(String sectionName);
}

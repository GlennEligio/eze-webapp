package com.eze.backend.restapi.repository;

import com.eze.backend.restapi.model.YearSection;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface YearSectionRepository extends JpaRepository<YearSection, Long> {

    Optional<YearSection> findBySectionName(String sectionName);
}

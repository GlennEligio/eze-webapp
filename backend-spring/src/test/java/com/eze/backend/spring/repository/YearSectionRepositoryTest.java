package com.eze.backend.spring.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.eze.backend.spring.model.YearLevel;
import com.eze.backend.spring.model.YearSection;
import com.eze.backend.spring.repository.YearSectionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

@DataJpaTest
public class YearSectionRepositoryTest {

    @Autowired
    private YearSectionRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private YearSection yearSection0;

    @BeforeEach
    void setup() {
        YearLevel yearLevel = new YearLevel(1, "First", false);
        yearSection0 = new YearSection("SectionName0", false, yearLevel);
        YearSection yearSection1 = new YearSection("SectionName1", true, yearLevel);
        entityManager.persist(yearLevel);
        entityManager.persist(yearSection0);
        entityManager.persist(yearSection1);
    }

    @Test
    @DisplayName("Find YearSection by Section name using valid name")
    void findBySectionName_usingValidSectionName_returnsSectionName() {
        String validSectionName = "SectionName0";

        Optional<YearSection> yearSectionOptional = repository.findBySectionName(validSectionName);

        assertTrue(yearSectionOptional.isPresent());
        assertEquals(yearSectionOptional.get(), yearSection0);
    }

    @Test
    @DisplayName("Find YearSection by Section name using invalid name")
    void findBySectionName_usingInvalidSectionName_returnsEmpty() {
        String invalidSectionName = "invalidSection";

        Optional<YearSection> yearSectionOptional = repository.findBySectionName(invalidSectionName);

        assertTrue(yearSectionOptional.isEmpty());
    }

    @Test
    @DisplayName("Find All Non deleted YearSections")
    void findAllNotDeleted_returnsNotDeletedYearSection() {
        List<YearSection> yearSectionList = repository.findAllNotDeleted();

        assertEquals(0, yearSectionList.stream().filter(YearSection::getDeleteFlag).count());
    }

    @Test
    @DisplayName("Soft deletes YearSection")
    void softDelete_updatesYearSectionDeleteFlag() {
        String validName = "SectionName0";

        repository.softDelete(validName);
        Optional<YearSection> yearSectionOptional = repository.findBySectionName(validName);

        assertTrue(yearSectionOptional.isPresent());
        assertTrue(yearSectionOptional.get().getDeleteFlag());
    }
}

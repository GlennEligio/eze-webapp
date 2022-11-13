package com.eze.backend.spring.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.eze.backend.spring.model.YearLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.Year;
import java.util.List;
import java.util.Optional;

@DataJpaTest
public class YearLevelRepositoryTest {

    @Autowired
    private YearLevelRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private YearLevel yearLevel1;

    @BeforeEach
    void setup() {
        yearLevel1 = new YearLevel(1, "First", false);
        YearLevel yearLevel2 = new YearLevel(2, "Second", true);
        entityManager.persist(yearLevel1);
        entityManager.persist(yearLevel2);
    }

    @Test
    @DisplayName("Find YearLevel by YearNumber using valid YearNumber")
    void findByYearNumber_usingValidYearNumber_returnsYearLevel() {
        Integer validYearNumber = 1;

        Optional<YearLevel> yearLevelOptional = repository.findByYearNumber(validYearNumber);

        assertTrue(yearLevelOptional.isPresent());
        assertEquals(yearLevelOptional.get(), yearLevel1);
    }

    @Test
    @DisplayName("Find YearLevel by YearNumber using invalid YearNumber")
    void findByYearNumber_usingInvalidYearNumber_returnEmpty() {
        Integer invalidYearNumber = 999;

        Optional<YearLevel> yearLevelOptional = repository.findByYearNumber(invalidYearNumber);

        assertTrue(yearLevelOptional.isEmpty());
    }

    @Test
    @DisplayName("Find All Non deleted YearLevels")
    void findAllNotDeleted_returnsNonDeletedYearLevels() {
        List<YearLevel> yearLevelList = repository.findAllNotDeleted();

        assertEquals(0, yearLevelList.stream().filter(YearLevel::getDeleteFlag).count());
    }

    @Test
    @DisplayName("Soft delete a YearLevel")
    void softDelete_updatesYearLevelDeleteFlag() {
        Integer validYearNumber = 1;

        repository.softDelete(1);
        Optional<YearLevel> yearLevelOptional = repository.findByYearNumber(validYearNumber);

        assertTrue(yearLevelOptional.isPresent());
        assertTrue(yearLevelOptional.get().getDeleteFlag());
    }
}

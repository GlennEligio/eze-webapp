package com.eze.backend.spring.service;

import static org.junit.jupiter.api.Assertions.*;

import com.eze.backend.spring.exception.ApiException;
import com.eze.backend.spring.model.YearLevel;
import com.eze.backend.spring.model.YearSection;
import com.eze.backend.spring.repository.YearLevelRepository;
import lombok.ToString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class YearLevelServiceTest {

    @Mock
    private YearLevelRepository repository;

    @InjectMocks
    private YearLevelService service;

    private YearLevel yearLevel1, yearLevel2;
    private List<YearLevel> yearLevelList;

    @BeforeEach
    void setup() {
        yearLevel1 = new YearLevel(1, "First", false);
        yearLevel2 = new YearLevel(2, "Second", true);
        YearSection yearSection0 = new YearSection("SectionName0", false, null);
        YearSection yearSection1 = new YearSection("SectionName1", false, null);
        yearLevel1.setYearSections(new ArrayList<>(List.of(yearSection0)));
        yearLevel2.setYearSections(new ArrayList<>(List.of(yearSection1)));
        yearLevelList = List.of(yearLevel1, yearLevel2);
    }

    @Test
    @DisplayName("Get all Year Level")
    void getAll_returnsYearLevels() {
        Mockito.when(repository.findAll()).thenReturn(yearLevelList);

        List<YearLevel> yearLevels = service.getAll();

        assertNotNull(yearLevels);
        assertEquals(yearLevelList, yearLevels);
    }

    @Test
    @DisplayName("Get all not deleted Year levels")
    void getAllNotDeleted_returnsNotDeletedYearLevels() {
        List<YearLevel> notDeletedYl = yearLevelList.stream().filter(yl -> !yl.getDeleteFlag()).toList();
        Mockito.when(repository.findAllNotDeleted()).thenReturn(notDeletedYl);

        List<YearLevel> yearLevels = service.getAllNotDeleted();

        assertNotNull(yearLevels);
        assertEquals(notDeletedYl, yearLevels);
    }

    @Test
    @DisplayName("Get YearLevel using valid year number")
    void get_usingValidYearNumber_returnsYearLevel() {
        Integer validYearNumber = yearLevel1.getYearNumber();
        Mockito.when(repository.findByYearNumber(validYearNumber)).thenReturn(Optional.of(yearLevel1));

        YearLevel yearLevel = service.get(validYearNumber);

        assertNotNull(yearLevel);
        assertEquals(yearLevel1, yearLevel);
    }

    @Test
    @DisplayName("Get YearLevel using invalid year number")
    void get_usingInvalidYearNumber_throwsException() {
        Integer invalidYearNumber = yearLevel1.getYearNumber();
        Mockito.when(repository.findByYearNumber(invalidYearNumber)).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> service.get(invalidYearNumber));
    }

    @Test
    @DisplayName("Create YearLevel using taken year number")
    void create_usingTakenYearNumber_throwsException() {
        Integer invalidYearNumber = yearLevel1.getYearNumber();
        Mockito.when(repository.findByYearNumber(invalidYearNumber)).thenReturn(Optional.of(yearLevel1));

        assertThrows(ApiException.class, () -> service.create(yearLevel1));
    }

    @Test
    @DisplayName("Create YearLevel using available year number")
    void create_usingAvailableYearNumber_returnsYearNumber() {
        Integer availableYearNumber = yearLevel1.getYearNumber();
        Mockito.when(repository.findByYearNumber(availableYearNumber)).thenReturn(Optional.empty());
        Mockito.when(repository.save(yearLevel1)).thenReturn(yearLevel1);

        YearLevel yearLevel = service.create(yearLevel1);

        assertNotNull(yearLevel);
        assertEquals(yearLevel, yearLevel1);
    }

    @Test
    @DisplayName("Create Year name for specific Year number")
    void createYearName_usingYearNumber_returnsCorrectYearName() {
        Integer yearNumber = yearLevel1.getYearNumber();
        String expectedYearName = "First";

        String resultYearName = service.createYearName(yearNumber);

        assertNotNull(resultYearName);
        assertEquals(expectedYearName, resultYearName);
    }
}

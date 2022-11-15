package com.eze.backend.spring.service;

import static org.junit.jupiter.api.Assertions.*;

import com.eze.backend.spring.enums.AccountType;
import com.eze.backend.spring.exception.ApiException;
import com.eze.backend.spring.model.Account;
import com.eze.backend.spring.model.Equipment;
import com.eze.backend.spring.model.YearLevel;
import com.eze.backend.spring.model.YearSection;
import com.eze.backend.spring.repository.YearLevelRepository;
import lombok.ToString;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
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

    @Test
    @DisplayName("Update existing Year level")
    void update_usingValidYearNumber_returnsUpdatedYearLevel() {
        Integer validYearNumber = yearLevel1.getYearNumber();
        YearLevel updatedYearLevel1 = new YearLevel(1, "First", true);
        YearSection yearSection0 = new YearSection("SectionName0", false, null);
        updatedYearLevel1.setYearSections(new ArrayList<>(List.of(yearSection0)));
        Mockito.when(repository.findByYearNumber(validYearNumber)).thenReturn(Optional.of(yearLevel1));
        Mockito.when(repository.save(updatedYearLevel1)).thenReturn(updatedYearLevel1);

        YearLevel updateYearLevelResult = service.update(updatedYearLevel1, validYearNumber);

        assertNotNull(updateYearLevelResult);
        assertEquals(updatedYearLevel1, updateYearLevelResult);
    }

    @Test
    @DisplayName("Update non-existent Year level")
    void update_usingInvalidYearNumber_throwsException() {
        Integer invalidYearNumber = yearLevel1.getYearNumber();
        Mockito.when(repository.findByYearNumber(invalidYearNumber)).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> service.update(yearLevel1, invalidYearNumber));
    }

    @Test
    @DisplayName("Delete existing Year Level")
    void delete_usingValidYearNumber_doesNotThrowException() {
        Integer validYearNumber = yearLevel1.getYearNumber();
        Mockito.when(repository.findByYearNumber(validYearNumber)).thenReturn(Optional.of(yearLevel1));

        assertDoesNotThrow(() -> service.delete(validYearNumber));
    }

    @Test
    @DisplayName("Delete non existing Year Level")
    void delete_usingInvalidYearNumber_throwsException() {
        Integer invalidYearNumber = yearLevel1.getYearNumber();
        Mockito.when(repository.findByYearNumber(invalidYearNumber)).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> service.delete(invalidYearNumber));
    }

    @Test
    @DisplayName("Soft deletes existing and not soft deleted Year Level")
    void softDelete_usingValidYearNumberAndDeleteFlagFalse_doesNotThrowException() {
        Integer validYearNumber = yearLevel1.getYearNumber();
        Mockito.when(repository.findByYearNumber(validYearNumber)).thenReturn(Optional.of(yearLevel1));

        assertDoesNotThrow(() -> service.softDelete(validYearNumber));
    }

    @Test
    @DisplayName("Soft deletes existing and soft deleted Year Level")
    void softDelete_usingValidYearNumberAndDeleteFlagTrue_throwException() {
        Integer validYearNumber = yearLevel1.getYearNumber();
        yearLevel1.setDeleteFlag(true);
        Mockito.when(repository.findByYearNumber(validYearNumber)).thenReturn(Optional.of(yearLevel1));

        assertThrows(ApiException.class, () -> service.softDelete(validYearNumber));
    }

    @Test
    @DisplayName("Soft deletes non existing Year Level ")
    void softDelete_usingInvalidYearNumber_throwsException() {
        Integer invalidYearNumber = yearLevel1.getYearNumber();
        Mockito.when(repository.findByYearNumber(invalidYearNumber)).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> service.softDelete(invalidYearNumber));
    }

    @Test
    @DisplayName("Create Not Found String")
    void notFound_returnsCorrectString() {
        Integer yearNumber = yearLevel1.getYearNumber();
        String notFoundExpected = "No YearLevel with year number " + yearNumber + " exist";

        String notFoundResult = service.notFound(yearNumber);

        assertNotNull(notFoundResult);
        assertEquals(notFoundExpected, notFoundResult);
    }

    @Test
    @DisplayName("Create Already Exist String")
    void alreadyExist_returnsCorrectString() {
        Integer yearNumber = yearLevel1.getYearNumber();
        String alreadyExistExpected = "YearLevel with year number " + yearNumber + " already exist";

        String alreadyExistResult = service.alreadyExist(yearNumber);

        assertNotNull(alreadyExistResult);
        assertEquals(alreadyExistExpected, alreadyExistResult);
    }

    @Test
    @DisplayName("Add or Update with same data and overwrite false")
    void addOrUpdate_usingSameDataAndOverwriteFalse_returnZero() {
        List<YearLevel> yearLevels = new ArrayList<>(yearLevelList);
        Mockito.when(repository.findByYearNumber(yearLevel1.getYearNumber())).thenReturn(Optional.of(yearLevel1));
        Mockito.when(repository.findByYearNumber(yearLevel2.getYearNumber())).thenReturn(Optional.of(yearLevel2));

        Integer itemsAffected = service.addOrUpdate(yearLevels, false);

        assertEquals(0, itemsAffected);
    }

    @Test
    @DisplayName("Add or Update with different data and overwrite false")
    void addOrUpdate_usingDifferentDataAndOverwriteFalse_returnsZero() {
        YearLevel updatedYearLevel1 = new YearLevel(yearLevel1.getYearNumber(), yearLevel1.getYearName(), true);
        List<YearLevel> yearLevels = List.of(updatedYearLevel1, yearLevel2);
        Mockito.when(repository.findByYearNumber(yearLevel1.getYearNumber())).thenReturn(Optional.of(yearLevel1));
        Mockito.when(repository.findByYearNumber(yearLevel2.getYearNumber())).thenReturn(Optional.of(yearLevel2));

        Integer itemsAffected = service.addOrUpdate(yearLevels, false);

        assertEquals(0, itemsAffected);
    }

    @Test
    @DisplayName("Add or Update with different data and overwrite true")
    void addOrUpdate_usingDifferentDataAndOverwriteFalse_returnsNonZero() {
        YearLevel updatedYearLevel1 = new YearLevel(yearLevel1.getYearNumber(), yearLevel1.getYearName(), true);
        List<YearLevel> yearLevels = List.of(updatedYearLevel1, yearLevel2);
        Mockito.when(repository.findByYearNumber(yearLevel1.getYearNumber())).thenReturn(Optional.of(yearLevel1));
        Mockito.when(repository.findByYearNumber(yearLevel2.getYearNumber())).thenReturn(Optional.of(yearLevel2));

        Integer itemsAffected = service.addOrUpdate(yearLevels, true);

        assertNotEquals(0, itemsAffected);
    }

    @Test
    @DisplayName("Create Excel from a List of Year Level")
    void listToExcel_returnsExcelWithSameData() {
        try {
            List<YearLevel> yearLevels = List.of(yearLevel1, yearLevel2);
            List<String> columns = List.of("Year level", "Year name", "Delete Flag");

            ByteArrayInputStream inputStream = service.listToExcel(yearLevels);
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheetAt(0);

            Row headerRow = sheet.getRow(0);
            for (int i = 0; i < columns.size(); i++) {
                String columnName = headerRow.getCell(i).getStringCellValue();
                assertEquals(columns.get(i), columnName);
            }

            for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                YearLevel yearLevel = yearLevels.get(i-1);
                Row row = sheet.getRow(i);

                assertEquals(yearLevel.getYearNumber(), (int) row.getCell(0).getNumericCellValue());
                assertEquals(yearLevel.getYearName(), row.getCell(1).getStringCellValue());
                assertEquals(yearLevel.getDeleteFlag(), row.getCell(2).getBooleanCellValue());
            }
        } catch (IOException ignored) {

        }
    }

    @Test
    @DisplayName("Create List of Year Level from Multipart file")
    void excelToList_returnsListOfAccount() {
        try {
            List<YearLevel> yearLevels = new ArrayList<>(yearLevelList);
            XSSFWorkbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Year Levels");

            List<String> columns = List.of("Year level", "Year name", "Delete Flag");

            // Creating header row
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columns.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns.get(i));
            }

            // Populating the Excel file with data
            for(int i=0; i < yearLevels.size(); i++) {
                Row dataRow = sheet.createRow(i+1);
                YearLevel yl = yearLevels.get(i);
                dataRow.createCell(0).setCellValue(yl.getYearNumber());
                dataRow.createCell(1).setCellValue(yl.getYearName());
                dataRow.createCell(2).setCellValue(yl.getDeleteFlag());
            }

            // Making size of the columns auto resize to fit data
            for (int i = 0; i < columns.size(); i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            MultipartFile file = new MockMultipartFile("file", new ByteArrayInputStream(outputStream.toByteArray()));

            List<YearLevel> yearLevelsResult = service.excelToList(file);

            assertNotEquals(0, yearLevelsResult.size());
            for (int i = 0; i < yearLevelsResult.size(); i++) {
                YearLevel yearLevelExpected = yearLevels.get(i);
                yearLevelExpected.setYearSections(null);
                YearLevel yearLevelResult = yearLevelsResult.get(i);
                assertEquals(yearLevelExpected.getYearNumber(), yearLevelResult.getYearNumber());
                assertEquals(yearLevelExpected.getDeleteFlag(), yearLevelResult.getDeleteFlag());
            }
        } catch (IOException ignored) {

        }
    }
}

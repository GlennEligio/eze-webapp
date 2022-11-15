package com.eze.backend.spring.service;

import static org.junit.jupiter.api.Assertions.*;

import com.eze.backend.spring.exception.ApiException;
import com.eze.backend.spring.model.Equipment;
import com.eze.backend.spring.model.YearLevel;
import com.eze.backend.spring.model.YearSection;
import com.eze.backend.spring.repository.YearSectionRepository;
import org.apache.poi.sl.draw.geom.GuideIf;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class YearSectionServiceTest {

    @Mock
    private YearSectionRepository repository;
    @Mock
    private YearLevelService ylService;

    @InjectMocks
    private YearSectionService service;

    private YearSection yearSection0, yearSection1;
    private YearLevel yearLevel;
    private List<YearSection> yearSectionList;

    @BeforeEach
    void setup() {
        yearLevel = new YearLevel(1, "First", false);
        yearSection0 = new YearSection("SectionName0", false, yearLevel);
        yearSection1 = new YearSection("SectionName1", true, yearLevel);
        yearSectionList = List.of(yearSection0, yearSection1);
    }

    @Test
    @DisplayName("Get all YearSections")
    void getAll_returnsYearSections() {
        Mockito.when(repository.findAll()).thenReturn(yearSectionList);

        List<YearSection> yearSections = service.getAll();

        assertNotNull(yearSections);
        assertEquals(yearSectionList, yearSections);
    }

    @Test
    @DisplayName("Get all not deleted YearSections")
    void getAllNotDeleted_returnsNotDeletedYearSections() {
        List<YearSection> notDeletedYearSections = yearSectionList.stream().filter(ys -> !ys.getDeleteFlag()).toList();
        Mockito.when(repository.findAllNotDeleted()).thenReturn(notDeletedYearSections);

        List<YearSection> yearSections = service.getAllNotDeleted();

        assertNotNull(yearSections);
        assertEquals(notDeletedYearSections, yearSections);
    }

    @Test
    @DisplayName("Get YearSection with valid SectionName")
    void get_usingValidSectionName_returnYearSection() {
        String sectionName = yearSection0.getSectionName();
        Mockito.when(repository.findBySectionName(sectionName)).thenReturn(Optional.of(yearSection0));

        YearSection yearSection = service.get(sectionName);

        assertNotNull(yearSection);
        assertEquals(yearSection0, yearSection);
    }

    @Test
    @DisplayName("Get YearSection with invalid SectionName")
    void get_usingInvalidSectionName_throwsException() {
        String invalidSectionName = yearSection0.getSectionName();
        Mockito.when(repository.findBySectionName(invalidSectionName)).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> service.get(invalidSectionName));
    }

    @Test
    @DisplayName("Create YearSection with available SectionName")
    void create_withAvailableSectionName_returnNewYearSection() {
        String validSectionName = yearSection0.getSectionName();
        Mockito.when(repository.findBySectionName(validSectionName)).thenReturn(Optional.empty());
        Mockito.when(repository.save(yearSection0)).thenReturn(yearSection0);
        Mockito.when(ylService.get(yearSection0.getYearLevel().getYearNumber())).thenReturn(yearSection0.getYearLevel());

        YearSection yearSection = service.create(yearSection0);

        assertNotNull(yearSection);
        assertEquals(yearSection0, yearSection);
    }

    @Test
    @DisplayName("Create YearSection with taken SectionName")
    void create_withTakenSectionName_throwsException() {
        String invalidSectionName = yearSection0.getSectionName();
        Mockito.when(repository.findBySectionName(invalidSectionName)).thenReturn(Optional.of(yearSection0));

        assertThrows(ApiException.class, () -> service.create(yearSection0));
    }

    @Test
    @DisplayName("Create YearSection with no YearLevel attached")
    void create_withNoYearLevelAttached_throwsException() {
        String validSectionName = yearSection0.getSectionName();
        yearSection0.setYearLevel(null);
        Mockito.when(repository.findBySectionName(validSectionName)).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> service.create(yearSection0));
    }

    @Test
    @DisplayName("Update non existent YearSection")
    void update_withNonExistentYearSection_throwsException() {
        String invalidSectionName = yearSection0.getSectionName();
        Mockito.when(repository.findBySectionName(invalidSectionName)).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> service.update(yearSection0, invalidSectionName));
    }

    @Test
    @DisplayName("Update existing YearSection")
    void update_withExistingYearSection_returnsUpdatedYearSection() {
        String validSectionName = yearSection0.getSectionName();
        YearSection updatedYearSection = new YearSection(yearSection0.getSectionName(), !yearSection0.getDeleteFlag(), yearSection0.getYearLevel());
        Mockito.when(repository.findBySectionName(validSectionName)).thenReturn(Optional.of(yearSection0));
        Mockito.when(repository.save(updatedYearSection)).thenReturn(updatedYearSection);

        YearSection yearSection = service.update(updatedYearSection, validSectionName);

        assertNotNull(yearSection);
        assertEquals(updatedYearSection, yearSection);
    }

    @Test
    @DisplayName("Delete existing YearSection")
    void delete_withValidSectionName_doesNotThrowException() {
        String validSectionName = yearSection0.getSectionName();
        Mockito.when(repository.findBySectionName(validSectionName)).thenReturn(Optional.of(yearSection0));

        assertDoesNotThrow(() -> service.delete(validSectionName));
    }

    @Test
    @DisplayName("Delete non existent YearSection")
    void delete_withInvalidSectionName_throwsException() {
        String invalidSectionName = yearSection0.getSectionName();
        Mockito.when(repository.findBySectionName(invalidSectionName)).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> service.delete(invalidSectionName));
    }

    @Test
    @DisplayName("Soft deletes a non existent YearSection")
    void softDelete_withNonExistentYearSection_throwsException() {
        String invalidSectionName = yearSection0.getSectionName();
        Mockito.when(repository.findBySectionName(invalidSectionName)).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> service.softDelete(invalidSectionName));
    }

    @Test
    @DisplayName("Soft deletes an existing and not soft deleted YearSection")
    void softDelete_withExistingAndNotYetDeletedYearSection_doesNotThrowException() {
        String validSectionName = yearSection0.getSectionName();
        Mockito.when(repository.findBySectionName(validSectionName)).thenReturn(Optional.of(yearSection0));

        assertDoesNotThrow(() -> service.softDelete(validSectionName));
    }

    @Test
    @DisplayName("Soft deletes an existing and already soft deleted YearSection")
    void softDelete_withExistingAndAlreadySoftDeletedYearSection_throwsException() {
        String validSectionName = yearSection0.getSectionName();
        yearSection0.setDeleteFlag(true);
        Mockito.when(repository.findBySectionName(validSectionName)).thenReturn(Optional.of(yearSection0));

        assertThrows(ApiException.class, () -> service.softDelete(validSectionName));
    }

    @Test
    @DisplayName("Add or Update using same data and overwrite false")
    void addOrUpdate_withSameDataAndOverrideFalse_returnsZero() {
        List<YearSection> yearSections = new ArrayList<>(yearSectionList);
        Mockito.when(repository.findBySectionName(yearSection0.getSectionName())).thenReturn(Optional.of(yearSection0));
        Mockito.when(repository.findBySectionName(yearSection1.getSectionName())).thenReturn(Optional.of(yearSection1));

        int itemsAffected = service.addOrUpdate(yearSections, false);

        assertEquals(0, itemsAffected);
    }

    @Test
    @DisplayName("Add or Update using different data and overwrite false")
    void addOrUpdate_withDifferentDataAndOverrideFalse_returnsZero() {
        yearSection0.setDeleteFlag(!yearSection0.getDeleteFlag());
        yearSection1.setDeleteFlag(!yearSection1.getDeleteFlag());
        List<YearSection> yearSections = List.of(yearSection0, yearSection1);
        Mockito.when(repository.findBySectionName(yearSection0.getSectionName())).thenReturn(Optional.of(yearSection0));
        Mockito.when(repository.findBySectionName(yearSection1.getSectionName())).thenReturn(Optional.of(yearSection1));

        int itemsAffected = service.addOrUpdate(yearSections, false);

        assertEquals(0, itemsAffected);
    }

    @Test
    @DisplayName("Add or Update using different data and overwrite true")
    void addOrUpdate_withDifferentDataAndOverwriteTrue_returnsNonZero() {
        YearSection updatedYearSection0 = new YearSection(yearSection0.getSectionName(), !yearSection0.getDeleteFlag(), yearSection0.getYearLevel());
        List<YearSection> yearSections = List.of(updatedYearSection0, yearSection1);
        Mockito.when(repository.findBySectionName(yearSection0.getSectionName())).thenReturn(Optional.of(yearSection0));
        Mockito.when(repository.findBySectionName(yearSection1.getSectionName())).thenReturn(Optional.of(yearSection1));

        int itemsAffected = service.addOrUpdate(yearSections, true);

        assertNotEquals(0, itemsAffected);
    }

    @Test
    @DisplayName("Create Excel from a List of Year Section")
    void listToExcel_returnsExcelWithSameData() {
        try {
            List<YearSection> yearSections = List.of(yearSection0, yearSection1);
            List<String> columns = List.of("Section name", "Year level", "Delete flag");

            ByteArrayInputStream inputStream = service.listToExcel(yearSections);
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheetAt(0);

            Row headerRow = sheet.getRow(0);
            for (int i = 0; i < columns.size(); i++) {
                String columnName = headerRow.getCell(i).getStringCellValue();
                assertEquals(columns.get(i), columnName);
            }

            for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                YearSection yearSection = yearSections.get(i-1);
                Row row = sheet.getRow(i);

                assertEquals(yearSection.getSectionName(), row.getCell(0).getStringCellValue());
                assertEquals(yearSection.getYearLevel().getYearNumber(), (int) row.getCell(1).getNumericCellValue());
                assertEquals(yearSection.getDeleteFlag(), row.getCell(2).getBooleanCellValue());
            }
        } catch (IOException ignored) {

        }
    }

    @Test
    @DisplayName("Create List of YearSection from Multipart file")
    void excelToList_returnsListOfAccount() {
        Mockito.when(ylService.get(yearLevel.getYearNumber())).thenReturn(yearLevel);

        try {
            List<YearSection> yearSections = new ArrayList<>(yearSectionList);
            XSSFWorkbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Year Sections");

            List<String> columns = List.of("Section name", "Year level", "Delete flag");

            // Creating header row
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columns.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns.get(i));
            }

            // Populating the Excel file with data
            for(int i=0; i < yearSections.size(); i++) {
                Row dataRow = sheet.createRow(i+1);
                YearSection yearSection = yearSections.get(i);
                dataRow.createCell(0).setCellValue(yearSection.getSectionName());
                dataRow.createCell(1).setCellValue(yearSection.getYearLevel().getYearNumber());
                dataRow.createCell(2).setCellValue(yearSection.getDeleteFlag());
            }

            // Making size of the columns auto resize to fit data
            for (int i = 0; i < columns.size(); i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            MultipartFile file = new MockMultipartFile("file", new ByteArrayInputStream(outputStream.toByteArray()));

            List<YearSection> yearSectionsResult = service.excelToList(file);

            assertNotEquals(0, yearSectionsResult.size());
            for (int i = 0; i < yearSectionsResult.size(); i++) {
                YearSection yearSectionExpected = yearSections.get(i);
                YearSection yearSectionResult = yearSectionsResult.get(i);
                assertEquals(yearSectionExpected.getSectionName(), yearSectionResult.getSectionName());
                assertEquals(yearSectionExpected.getDeleteFlag(), yearSectionResult.getDeleteFlag());
                assertEquals(yearSectionExpected.getYearLevel(), yearSectionResult.getYearLevel());
            }
        } catch (IOException ignored) {

        }
    }
}

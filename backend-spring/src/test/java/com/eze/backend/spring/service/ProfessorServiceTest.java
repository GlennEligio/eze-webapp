package com.eze.backend.spring.service;

import static org.junit.jupiter.api.Assertions.*;

import com.eze.backend.spring.enums.AccountType;
import com.eze.backend.spring.exception.ApiException;
import com.eze.backend.spring.model.Account;
import com.eze.backend.spring.model.Professor;
import com.eze.backend.spring.model.YearSection;
import com.eze.backend.spring.repository.ProfessorRepository;
import com.eze.backend.spring.util.ObjectIdGenerator;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ProfessorServiceTest {

    @Mock
    private ProfessorRepository repository;

    @Mock
    private ObjectIdGenerator idGenerator;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private ProfessorService service;

    private List<Professor> professorList;
    private Professor professor0, professor1;
    private Account account0, account1;

    @BeforeEach
    void setup() {
        professor0 = new Professor("Name0", "+639062560574", false, "email0@gmail.com", "https://sampleimage0.com");
        professor1 = new Professor("Name1", "+639062560574", true, "email1@gmail.com", "https://sampleimage1.com");
        account0 = new Account(0L, professor0.getName(), professor0.getName(), professor0.getEmail(), "EncryptedPassword", AccountType.PROF, professor0.getProfile(), LocalDateTime.now(), true, false);
        account1 = new Account(1L, professor1.getName(), professor1.getName(), professor1.getEmail(), "EncryptedPassword", AccountType.PROF, professor1.getProfile(), LocalDateTime.now(), true, false);
        professorList = List.of(professor1, professor0);
    }

    @Test
    @DisplayName("Get all Professors")
    void getAll_returnsProfessors() {
        Mockito.when(repository.findAll()).thenReturn(professorList);

        List<Professor> professors = service.getAll();

        assertNotNull(professors);
        assertEquals(professorList, professors);
    }

    @Test
    @DisplayName("Get all not deleted Professors")
    void getAllNotDeleted_returnsNotDeletedProfessors() {
        List<Professor> notDeletedProfessors = professorList.stream().filter(p -> !p.getDeleteFlag()).toList();
        Mockito.when(repository.findAllNotDeleted()).thenReturn(notDeletedProfessors);

        List<Professor> professors = service.getAllNotDeleted();

        assertNotNull(professors);
        assertEquals(notDeletedProfessors, professors);
    }

    @Test
    @DisplayName("Get Professor using valid name")
    void get_usingValidName_returnsProfessor() {
        String validName = professor0.getName();
        Mockito.when(repository.findByName(validName)).thenReturn(Optional.of(professor0));

        Professor professor = service.get(validName);

        assertNotNull(professor);
        assertEquals(professor0, professor);
    }

    @Test
    @DisplayName("Get Professor using invalid name")
    void get_usingInvalidName_throwsException() {
        String invalidName = professor0.getName();
        Mockito.when(repository.findByName(invalidName)).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> service.get(invalidName));
    }

    @Test
    @DisplayName("Create Professor using available name")
    void create_usingAvailableName_returnsNewProfessor() {
        String availableName = professor0.getName();
        Mockito.when(repository.findByName(availableName)).thenReturn(Optional.empty());
        Mockito.when(repository.save(professor0)).thenReturn(professor0);
        Mockito.when(idGenerator.createId()).thenReturn(account0.getPassword());
        Account accountToCreate = new Account(null, professor0.getName(), professor0.getName(), professor0.getEmail(), "EncryptedPassword", AccountType.PROF, professor0.getProfile(), null, null, null);
        Mockito.when(accountService.create(accountToCreate)).thenReturn(account0);

        Professor professor = service.create(professor0);

        assertNotNull(professor);
        assertEquals(professor0, professor);
    }

    @Test
    @DisplayName("Create Professor using taken name")
    void create_usingTakenName_throwsException() {
        String takenName = professor0.getName();
        Mockito.when(repository.findByName(takenName)).thenReturn(Optional.of(professor0));

        assertThrows(ApiException.class, () -> service.create(professor0));
    }

    @Test
    @DisplayName("Update non existent Professor")
    void update_withNonExistentProfessor_throwsException() {
        String invalidName = professor0.getName();
        Mockito.when(repository.findByName(invalidName)).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> service.update(professor0, invalidName));
    }

    @Test
    @DisplayName("Update existing Professor")
    void update_withExistingProfessor_returnsUpdatedProfessor() {
        String validName = professor0.getName();
        Professor updatedProfessor = new Professor(professor0.getName(), professor0.getContactNumber(), !professor0.getDeleteFlag(), professor0.getEmail(), professor0.getProfile());
        Mockito.when(repository.findByName(validName)).thenReturn(Optional.of(professor0));
        Mockito.when(repository.save(updatedProfessor)).thenReturn(updatedProfessor);

        Professor professor = service.update(updatedProfessor, validName);

        assertNotNull(professor);
        assertEquals(updatedProfessor, professor);
    }

    @Test
    @DisplayName("Delete non existent Professor")
    void delete_withNonExistingProfessor_throwsException() {
        String invalidName = professor0.getName();
        Mockito.when(repository.findByName(invalidName)).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> service.delete(invalidName));
    }

    @Test
    @DisplayName("Delete existing Professor")
    void delete_withExistingProfessor_doesNotThrowException() {
        String validName = professor0.getName();
        Mockito.when(repository.findByName(validName)).thenReturn(Optional.of(professor0));

        assertDoesNotThrow(() -> service.delete(validName));
    }

    @Test
    @DisplayName("Soft deleting non existing Professor")
    void softDelete_withNonExistingProfessor_throwsException() {
        String invalidName = professor0.getName();
        Mockito.when(repository.findByName(invalidName)).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> service.softDelete(invalidName));
    }

    @Test
    @DisplayName("Soft deleting existing and soft deleted Professor")
    void softDelete_withExistingAndAlreadySoftDeletedProfessor_throwsException() {
        String validName = professor0.getName();
        professor0.setDeleteFlag(true);
        Mockito.when(repository.findByName(validName)).thenReturn(Optional.of(professor0));

        assertThrows(ApiException.class, () -> service.softDelete(validName));
    }

    @Test
    @DisplayName("Soft deleting existing and not yet soft deleted Professor")
    void softDelete_withExistingAndNotYetSoftDeletedProfessor_throwsException() {
        String validName = professor0.getName();
        professor0.setDeleteFlag(false);
        Mockito.when(repository.findByName(validName)).thenReturn(Optional.of(professor0));

        assertDoesNotThrow(() -> service.softDelete(validName));
    }

    @Test
    @DisplayName("Create Not Found string")
    void notFound_returnsCorrectString() {
        String name = professor0.getName();
        String expectedString = "No professor with name " + name + " was found";

        String resultString = service.notFound(name);

        assertNotNull(resultString);
        assertEquals(expectedString, resultString);
    }

    @Test
    @DisplayName("Create Already Exist string")
    void alreadyExist_returnCorrectString() {
        String name = professor0.getName();
        String expectedString = "Professor with name " + name + " already exist";

        String resultString = service.alreadyExist(name);

        assertNotNull(resultString);
        assertEquals(expectedString, resultString);
    }

    @Test
    @DisplayName("Add or Update with same data and overwrite false")
    void addOrUpdate_withSameDataAndOverwriteFalse_returnZero() {
        List<Professor> professors = new ArrayList<>(professorList);
        Mockito.when(repository.findByName(professor0.getName())).thenReturn(Optional.of(professor0));
        Mockito.when(repository.findByName(professor1.getName())).thenReturn(Optional.of(professor1));

        int itemsAffected = service.addOrUpdate(professors, false);

        assertEquals(0, itemsAffected);
    }

    @Test
    @DisplayName("Add or Update with different data and overwrite false")
    void addOrUpdate_withDifferentDataAndOverwriteFalse_returnZero() {
        Professor updatedProfessor0 = new Professor(professor0.getName(), professor0.getContactNumber(), !professor0.getDeleteFlag(), professor0.getEmail(), professor0.getProfile());
        List<Professor> professors = List.of(updatedProfessor0, professor1);
        Mockito.when(repository.findByName(professor0.getName())).thenReturn(Optional.of(professor0));
        Mockito.when(repository.findByName(professor1.getName())).thenReturn(Optional.of(professor1));

        int itemsAffected = service.addOrUpdate(professors, false);

        assertEquals(0, itemsAffected);
    }

    @Test
    @DisplayName("Add or Update with different data and overwrite true")
    void addOrUpdate_withDifferentDataAndOverwriteFalse_returnsNonZero() {
        Professor updatedProfessor0 = new Professor(professor0.getName(), professor0.getContactNumber(), !professor0.getDeleteFlag(), professor0.getEmail(), professor0.getProfile());
        List<Professor> professors = List.of(updatedProfessor0, professor1);
        Mockito.when(repository.findByName(professor0.getName())).thenReturn(Optional.of(professor0));
        Mockito.when(repository.findByName(professor1.getName())).thenReturn(Optional.of(professor1));

        int itemsAffected = service.addOrUpdate(professors, true);

        assertNotEquals(0, itemsAffected);
    }

    @Test
    @DisplayName("Create Excel from a List of Professor")
    void listToExcel_returnsExcelWithSameData() {
        try {
            professor0.setId(0L);
            professor1.setId(1L);
            List<Professor> professors = List.of(professor0, professor1);
            List<String> columns = List.of("ID", "Name", "Contact Number", "Delete flag", "Email", "Profile image url");

            ByteArrayInputStream inputStream = service.listToExcel(professors);
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheetAt(0);

            Row headerRow = sheet.getRow(0);
            for (int i = 0; i < columns.size(); i++) {
                String columnName = headerRow.getCell(i).getStringCellValue();
                assertEquals(columns.get(i), columnName);
            }

            for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                Professor professor = professors.get(i-1);
                Row row = sheet.getRow(i);

                assertEquals(professor.getId(), (long) row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getNumericCellValue());
                assertEquals(professor.getName(), row.getCell(1).getStringCellValue());
                assertEquals(professor.getContactNumber(), row.getCell(2).getStringCellValue());
                assertEquals(professor.getDeleteFlag(), row.getCell(3).getBooleanCellValue());
                assertEquals(professor.getEmail(), row.getCell(4).getStringCellValue());
                assertEquals(professor.getProfile(), row.getCell(5).getStringCellValue());
            }
        } catch (IOException ignored) {

        }
    }

    @Test
    @DisplayName("Create List of Professors from Multipart file")
    void excelToList_returnsListOfAccount() {
        try {
            professor0.setId(0L);
            professor1.setId(1L);
            List<Professor> professors = List.of(professor0, professor1);
            XSSFWorkbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Professors");

            List<String> columns = List.of("ID", "Name", "Contact Number", "Delete flag", "Email", "Profile image url");

            // Creating header row
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columns.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns.get(i));
            }

            // Populating the Excel file with data
            for(int i=0; i < professors.size(); i++) {
                Row dataRow = sheet.createRow(i+1);
                Professor prof = professors.get(i);
                dataRow.createCell(0).setCellValue(prof.getId());
                dataRow.createCell(1).setCellValue(prof.getName());
                dataRow.createCell(2).setCellValue(prof.getContactNumber());
                dataRow.createCell(3).setCellValue(prof.getDeleteFlag());
                dataRow.createCell(4).setCellValue(prof.getEmail());
                dataRow.createCell(5).setCellValue(prof.getProfile());
            }

            // Making size of the columns auto resize to fit data
            for (int i = 0; i < columns.size(); i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            MultipartFile file = new MockMultipartFile("file", new ByteArrayInputStream(outputStream.toByteArray()));

            List<Professor> professorsResult = service.excelToList(file);

            assertNotEquals(0, professorsResult.size());
            for (int i = 0; i < professorsResult.size(); i++) {
                Professor professorExpected = professors.get(i);
                Professor professorResult = professorsResult.get(i);
                assertEquals(professorExpected, professorResult);
            }
        } catch (IOException ignored) {

        }
    }
}

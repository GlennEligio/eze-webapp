package com.eze.backend.spring.service;

import static org.junit.jupiter.api.Assertions.*;

import com.eze.backend.spring.dtos.StudentListDto;
import com.eze.backend.spring.enums.AccountType;
import com.eze.backend.spring.exception.ApiException;
import com.eze.backend.spring.model.Account;
import com.eze.backend.spring.model.Student;
import com.eze.backend.spring.model.YearLevel;
import com.eze.backend.spring.model.YearSection;
import com.eze.backend.spring.repository.StudentRepository;
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
public class StudentServiceTest {

    @Mock
    private StudentRepository repository;
    @Mock
    private YearSectionService ysService;
    @Mock
    private YearLevelService ylService;
    @Mock
    private ObjectIdGenerator idGenerator;
    @Mock
    private AccountService accountService;

    @InjectMocks
    private StudentService service;

    private Student student0, student1;
    private Account account0, account1;
    private YearLevel yearLevel;
    private YearSection yearSection;
    private List<Student> studentList;

    @BeforeEach
    void setup() {
        yearLevel = new YearLevel(1, "First", false);
        yearSection = new YearSection("SectionName1", false, yearLevel);
        student0 = new Student("2015-00129-MN-00", "FullName0", yearSection, "09062560574", "Birthday0", "Address0", "email0@gmail.com", "Guardian0", "GuardianNumber0", yearLevel, "https://sampleprofile0.com", false);
        student1 = new Student("2015-00129-MN-01", "FullName1", yearSection, "09062560571", "Birthday1", "Address1", "email1@gmail.com", "Guardian1", "GuardianNumber1", yearLevel, "https://sampleprofile1.com", true);
        account0 = new Account(0L, student0.getFullName(), student0.getStudentNumber(), student0.getEmail(), "EncryptedPassword", AccountType.STUDENT, student0.getProfile(), LocalDateTime.now(), true, false);
        account1 = new Account(1L, student1.getFullName(), student1.getStudentNumber(), student1.getEmail(), "EncryptedPassword", AccountType.STUDENT, student1.getProfile(), LocalDateTime.now(), true, false);
        student0.setStudentAccount(account0);
        student1.setStudentAccount(account1);
        studentList = List.of(student1, student0);
    }

    @Test
    @DisplayName("Get all Students")
    void getAll_returnsStudents() {
        Mockito.when(repository.findAll()).thenReturn(studentList);

        List<Student> students = service.getAll();

        assertNotNull(students);
        assertEquals(studentList, students);
    }

    @Test
    @DisplayName("Get all not deleted Students")
    void getAllNotDeleted_returnsNotDeletedStudents() {
        List<Student> notDeleted = studentList.stream().filter(s -> !s.getDeleteFlag()).toList();
        Mockito.when(repository.findAllNotDeleted()).thenReturn(notDeleted);

        List<Student> students = service.getAllNotDeleted();

        assertNotNull(students);
        assertEquals(notDeleted, students);
    }

    @Test
    @DisplayName("Get Student using invalid Student number")
    void get_usingInvalidStudentNumber_throwsException() {
        String invalidStudentNumber = student0.getStudentNumber();
        Mockito.when(repository.findByStudentNumber(invalidStudentNumber)).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> service.get(invalidStudentNumber));
    }

    @Test
    @DisplayName("Get Student using valid Student Number")
    void get_usingValidStudentNumber_returnsStudent() {
        String validStudentNumber = student0.getStudentNumber();
        Mockito.when(repository.findByStudentNumber(validStudentNumber)).thenReturn(Optional.of(student0));

        Student student = service.get(validStudentNumber);

        assertNotNull(student);
        assertEquals(student0, student);
    }

    @Test
    @DisplayName("Create Student using taken Student Number")
    void create_usingTakenStudentNumber_throwsException() {
        String takenStudentNumber = student0.getStudentNumber();
        Mockito.when(repository.findByStudentNumber(takenStudentNumber)).thenReturn(Optional.of(student0));

        assertThrows(ApiException.class, () -> service.create(student0));
    }

    @Test
    @DisplayName("Create Student using available Student Number")
    void create_usingAvailableStudentNumber_returnsNewStudent() {
        String availableStudentNumber = student0.getStudentNumber();
        Account newAccountToCreate = new Account(null, student0.getFullName(), student0.getStudentNumber(), student0.getEmail(), "EncryptedPassword", AccountType.STUDENT, student0.getProfile(), null, null, null);
        Mockito.when(repository.findByStudentNumber(availableStudentNumber)).thenReturn(Optional.empty());
        Mockito.when(ysService.get(student0.getYearAndSection().getSectionName())).thenReturn(student0.getYearAndSection());
        Mockito.when(ylService.get(student0.getYearLevel().getYearNumber())).thenReturn(student0.getYearLevel());
        Mockito.when(repository.save(student0)).thenReturn(student0);
        Mockito.when(idGenerator.createId()).thenReturn(account0.getPassword());
        Mockito.when(accountService.create(newAccountToCreate)).thenReturn(account0);

        Student student = service.create(student0);

        assertNotNull(student);
        assertEquals(student0, student);
    }

    @Test
    @DisplayName("Update non existent Student")
    void update_usingInvalidStudentNumber_throwsException() {
        String invalidStudentNumber = student0.getStudentNumber();
        Mockito.when(repository.findByStudentNumber(invalidStudentNumber)).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> service.update(student0, invalidStudentNumber));
    }

    @Test
    @DisplayName("Update existing Student")
    void update_usingValidStudentNumber_returnsUpdatedStudent() {
        String validStudentNumber = student0.getStudentNumber();
        Mockito.when(repository.findByStudentNumber(validStudentNumber)).thenReturn(Optional.of(student0));
        Mockito.when(ysService.get(yearSection.getSectionName())).thenReturn(yearSection);
        Mockito.when(ylService.get(yearLevel.getYearNumber())).thenReturn(yearLevel);
        Mockito.when(repository.save(student0)).thenReturn(student0);

        Student student = service.update(student0, validStudentNumber);

        assertNotNull(student);
        assertEquals(student0, student);
    }

    @Test
    @DisplayName("Delete non existent Student")
    void delete_nonExistentStudent_throwsException() {
        String invalidStudentNumber = student0.getStudentNumber();
        Mockito.when(repository.findByStudentNumber(invalidStudentNumber)).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> service.delete(invalidStudentNumber));
    }

    @Test
    @DisplayName("Delete existing Student")
    void delete_existingStudent_doesNotThrowException() {
        String validStudentNumber = student0.getStudentNumber();
        Mockito.when(repository.findByStudentNumber(validStudentNumber)).thenReturn(Optional.of(student0));

        assertDoesNotThrow(() -> service.delete(validStudentNumber));
    }

    @Test
    @DisplayName("Soft delete non existent Student")
    void softDelete_nonExistentStudent_throwsException() {
        String invalidStudentNumber = student0.getStudentNumber();
        Mockito.when(repository.findByStudentNumber(invalidStudentNumber)).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> service.softDelete(invalidStudentNumber));
    }

    @Test
    @DisplayName("Soft delete existing and already soft deleted Student")
    void softDelete_existingAndSoftDeletedStudent_throwsException() {
        String validStudentNumber = student0.getStudentNumber();
        student0.setDeleteFlag(true);
        Mockito.when(repository.findByStudentNumber(validStudentNumber)).thenReturn(Optional.of(student0));

        assertThrows(ApiException.class, () -> service.softDelete(validStudentNumber));
    }

    @Test
    @DisplayName("Soft delete existing and not yet soft deleted Student")
    void softDelete_existingAndNotYetSoftDeletedStudent_doesNotThrowException() {
        String validStudentNumber = student0.getStudentNumber();
        student0.setDeleteFlag(false);
        Mockito.when(repository.findByStudentNumber(validStudentNumber)).thenReturn(Optional.of(student0));

        assertDoesNotThrow(() -> service.softDelete(validStudentNumber));
    }

    @Test
    @DisplayName("Create Not Found string")
    void notFound_createCorrectString() {
        String studentNumber = student0.getStudentNumber();
        String expected = "No student with student number " + studentNumber + " was found";

        String result = service.notFound(studentNumber);

        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Create Already Exist string")
    void alreadyExist_createCorrectString() {
        String studentNumber = student0.getStudentNumber();
        String expected = "Student with student number " + studentNumber + " already exist";

        String result = service.alreadyExist(studentNumber);

        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Add or Update with same data and overwrite false")
    void addOrUpdate_usingSameDataAndOverwriteFalse_returnsZero() {
        List<Student> students = new ArrayList<>(studentList);
        Mockito.when(repository.findByStudentNumber(student0.getStudentNumber())).thenReturn(Optional.of(student0));
        Mockito.when(repository.findByStudentNumber(student1.getStudentNumber())).thenReturn(Optional.of(student1));

        int itemsAffected = service.addOrUpdate(students, false);

        assertEquals(0, itemsAffected);
    }

    @Test
    @DisplayName("Add or Update with different data and overwrite false")
    void addOrUpdate_usingDifferentDataAndOverwriteFalse_returnsZero() {
        Student updatedStudent0 = new Student(student0.getStudentNumber(), student0.getFullName(), student0.getYearAndSection(), student0.getContactNumber(), student0.getBirthday(), student0.getAddress(), student0.getEmail(), student0.getGuardian(), student0.getGuardianNumber(), student0.getYearLevel(), student0.getProfile(), !student0.getDeleteFlag());
        List<Student> students = List.of(updatedStudent0, student1);
        Mockito.when(repository.findByStudentNumber(student0.getStudentNumber())).thenReturn(Optional.of(student0));
        Mockito.when(repository.findByStudentNumber(student1.getStudentNumber())).thenReturn(Optional.of(student1));

        int itemsAffected = service.addOrUpdate(students, false);

        assertEquals(0, itemsAffected);
    }

    @Test
    @DisplayName("Add or Update with different data and overwrite true")
    void addOrUpdate_usingDifferentDataAndOverwriteTrue_returnsNonZero() {
        Student updatedStudent0 = new Student(student0.getStudentNumber(), student0.getFullName(), student0.getYearAndSection(), student0.getContactNumber(), student0.getBirthday(), student0.getAddress(), student0.getEmail(), student0.getGuardian(), student0.getGuardianNumber(), student0.getYearLevel(), student0.getProfile(), !student0.getDeleteFlag());
        updatedStudent0.setId(0L);
        student1.setId(1L);
        List<Student> students = List.of(updatedStudent0, student1);
        Mockito.when(repository.findByStudentNumber(student0.getStudentNumber())).thenReturn(Optional.of(student0));
        Mockito.when(repository.findByStudentNumber(student1.getStudentNumber())).thenReturn(Optional.of(student1));

        int itemsAffected = service.addOrUpdate(students, true);

        assertNotEquals(0, itemsAffected);
    }

    @Test
    @DisplayName("Create Excel from a List of Student")
    void listToExcel_returnsExcelWithSameData() {
        try {
            student0.setId(0L);
            student1.setId(1L);
            List<Student> students = List.of(student0, student1);
            List<String> columns = List.of("ID", "Student number", "Full name", "Year and section", "Contact number", "Birthday", "Address", "Email", "Guardian", "Guardian number", "Year level", "Delete Flag", "Profile image url");

            ByteArrayInputStream inputStream = service.listToExcel(students);
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheetAt(0);

            Row headerRow = sheet.getRow(0);
            for (int i = 0; i < columns.size(); i++) {
                String columnName = headerRow.getCell(i).getStringCellValue();
                assertEquals(columns.get(i), columnName);
            }

            for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                Student student = students.get(i-1);
                Row row = sheet.getRow(i);

                assertEquals(student.getId(), (long) row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getNumericCellValue());
                assertEquals(student.getStudentNumber(), row.getCell(1).getStringCellValue());
                assertEquals(student.getFullName(), row.getCell(2).getStringCellValue());
                assertEquals(student.getYearAndSection().getSectionName(), row.getCell(3).getStringCellValue());

                assertEquals(student.getContactNumber(), row.getCell(4).getStringCellValue());
                assertEquals(student.getBirthday(), row.getCell(5).getStringCellValue());
                assertEquals(student.getAddress(), row.getCell(6).getStringCellValue());
                assertEquals(student.getEmail(), row.getCell(7).getStringCellValue());
                assertEquals(student.getGuardian(), row.getCell(8).getStringCellValue());
                assertEquals(student.getGuardianNumber(), row.getCell(9).getStringCellValue());

                assertEquals(student.getYearLevel().getYearNumber(), (int) row.getCell(10).getNumericCellValue());

                assertEquals(student.getDeleteFlag(), row.getCell(11).getBooleanCellValue());
                assertEquals(student.getProfile(), row.getCell(12).getStringCellValue());
            }
        } catch (IOException ignored) {

        }
    }

    @Test
    @DisplayName("Create List of Students from Multipart file")
    void excelToList_returnsListOfAccount() {
        Mockito.when(ysService.get(yearSection.getSectionName())).thenReturn(yearSection);
        Mockito.when(ylService.get(yearLevel.getYearNumber())).thenReturn(yearLevel);
        try {
            student0.setId(0L);
            student1.setId(1L);
            List<Student> students = List.of(student0, student1);
            List<String> columns = List.of("ID", "Student number", "Full name", "Year and section", "Contact number", "Birthday", "Address", "Email", "Guardian", "Guardian number", "Year level", "Delete Flag", "Profile image url");
            XSSFWorkbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Students");


            // Creating header row
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columns.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns.get(i));
            }

            // Populating the Excel file with data
            for(int i=0; i < students.size(); i++) {
                Row dataRow = sheet.createRow(i+1);
                StudentListDto student = Student.toStudentListDto(students.get(i));
                dataRow.createCell(0).setCellValue(student.getId());
                dataRow.createCell(1).setCellValue(student.getStudentNumber());
                dataRow.createCell(2).setCellValue(student.getFullName());
                dataRow.createCell(3).setCellValue(student.getYearAndSection());
                dataRow.createCell(4).setCellValue(student.getContactNumber());
                dataRow.createCell(11).setCellValue(students.get(i).getDeleteFlag());
                dataRow.createCell(12).setCellValue(students.get(i).getProfile());

                // Nullable properties check
                if(student.getBirthday() != null) dataRow.createCell(5).setCellValue(student.getBirthday());
                if(student.getAddress() != null) dataRow.createCell(6).setCellValue(student.getAddress());
                if(student.getEmail() != null) dataRow.createCell(7).setCellValue(student.getEmail());
                if(student.getGuardian() != null) dataRow.createCell(8).setCellValue(student.getGuardian());
                if(student.getGuardianNumber() != null) dataRow.createCell(9).setCellValue(student.getGuardianNumber());
                if(student.getYearLevel() != null) dataRow.createCell(10).setCellValue(student.getYearLevel());

            }

            // Making size of the columns auto resize to fit data
            for (int i = 0; i < columns.size(); i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            MultipartFile file = new MockMultipartFile("file", new ByteArrayInputStream(outputStream.toByteArray()));

            List<Student> studentsResult = service.excelToList(file);

            assertNotEquals(0, studentsResult.size());
            for (int i = 0; i < studentsResult.size(); i++) {
                Student studentResult = studentsResult.get(i);
                Student studentExpected = students.get(i);
                // Set the Account to null since Account information is not included in Excel file
                studentExpected.setStudentAccount(null);
                assertEquals(studentExpected, studentResult);
            }
        } catch (IOException ignored) {

        }
    }
}

package com.eze.backend.spring.controller;

import com.eze.backend.spring.dtos.StudentDto;
import com.eze.backend.spring.dtos.StudentListDto;
import com.eze.backend.spring.exception.ApiException;
import com.eze.backend.spring.model.Student;
import com.eze.backend.spring.model.YearLevel;
import com.eze.backend.spring.model.YearSection;
import com.eze.backend.spring.service.StudentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class StudentControllerTest {

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private StudentService service;

    private final static String CORRECT_CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    private static final String INCORRECT_CONTENT_TYPE = "application/vnd.ms-excels";

    private MockMvc mockMvc;
    private ObjectMapper mapper;
    private YearLevel yearLevel;
    private YearSection yearSection;
    private Student std0, std1;
    private List<Student> studentList;

    @BeforeAll
    void setupAll() {
        mapper = new ObjectMapper();
        mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @BeforeEach
    void setup() {
        yearLevel = new YearLevel(1, "First", false);
        yearSection = new YearSection("SectionName1", false, yearLevel);
        std0 = new Student("2015-00129-MN-00", "FullName0", yearSection, "09062560574", "Birthday0", "Address0", "Email0", "Guardian0", "GuardianNumber0", yearLevel, "https://sampleprofile0.com", false);
        std1 = new Student("2015-00129-MN-01", "FullName1", yearSection, "09062560571", "Birthday1", "Address1", "Email1", "Guardian1", "GuardianNumber1", yearLevel, "https://sampleprofile1.com", true);
        studentList = List.of(std0, std1);
    }

    @Test
    @DisplayName("Get all Students using invalid Auth")
    void getStudents_withInvalidAuth_returns403Forbidden() throws Exception {
        mockMvc.perform(get("/api/v1/students"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Get all Students with valid Auth")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void getStudents_withValidAuth_returns200OK() throws Exception {
        List<Student> studentsNotDeleted = studentList.stream().filter(s -> !s.getDeleteFlag()).toList();
        List<StudentListDto> studentListDtos = studentsNotDeleted.stream().map(Student::toStudentListDto).toList();
        when(service.getAllNotDeleted()).thenReturn(studentsNotDeleted);
        String responseJson = mapper.writeValueAsString(studentListDtos);

        mockMvc.perform(get("/api/v1/students"))
                .andExpect(status().isOk())
                .andExpect(content().json(responseJson));
    }

    @Test
    @DisplayName("Download Students with invalid Auth")
    void download_withInvalidAuth_returns403Forbidden() throws Exception {
        mockMvc.perform(get("/api/v1/students/download"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Download Students with valid Auth")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void download_withValidAuth_returns200OK() throws Exception {
        std0.setId(0L);
        std1.setId(1L);
        MockMultipartFile multipartFile = createMultipartFile(studentList, CORRECT_CONTENT_TYPE);
        when(service.getAll()).thenReturn(studentList);
        when(service.listToExcel(studentList)).thenReturn(new ByteArrayInputStream(multipartFile.getBytes()));

        mockMvc.perform(get("/api/v1/students/download"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM));
    }

    @Test
    @DisplayName("Upload Students with invalid Auth")
    void upload_withInvalidAuth_returns403Forbidden() throws Exception {
        std0.setId(0L);
        std1.setId(1L);
        MockMultipartFile multipartFile = createMultipartFile(studentList, CORRECT_CONTENT_TYPE);

        mockMvc.perform(multipart(HttpMethod.POST, "/api/v1/students/upload")
                        .file(multipartFile))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Upload Students using file with invalid Content type and valid Auth")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void upload_withValidAuthAndIncorrectContentType_returns400BadRequest() throws Exception {
        std0.setId(0L);
        std1.setId(1L);
        MockMultipartFile multipartFile = createMultipartFile(studentList, INCORRECT_CONTENT_TYPE);

        mockMvc.perform(multipart(HttpMethod.POST, "/api/v1/students/upload")
                        .file(multipartFile))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Upload Students using file with correct Content type and valid Auth")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void upload_withValidAuthAndCorrectContentType_returns200OK() throws Exception {
        std0.setId(0L);
        std1.setId(1L);
        MockMultipartFile multipartFile = createMultipartFile(studentList, CORRECT_CONTENT_TYPE);
        when(service.excelToList(multipartFile)).thenReturn(studentList);
        when(service.addOrUpdate(studentList, false)).thenReturn(0);
        ObjectNode response = mapper.createObjectNode();
        response.put("Students Affected", 0);
        String responseJson = mapper.writeValueAsString(response);

        mockMvc.perform(multipart(HttpMethod.POST, "/api/v1/students/upload")
                        .file(multipartFile))
                .andExpect(status().isOk())
                .andExpect(content().json(responseJson));
    }

    @Test
    @DisplayName("Get Student with invalid Auth")
    void getStudent_withInvalidAuth_returns403Forbidden() throws Exception {
        String validStudentNumber = std0.getStudentNumber();

        mockMvc.perform(get("/api/v1/students/" + validStudentNumber))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Get non existent Student with valid Auth")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void getStudent_withValidAuthAndInvalidStudentNumber_returns404NotFound() throws Exception {
        String invalidStudentNumber = std0.getStudentNumber();
        when(service.get(invalidStudentNumber)).thenThrow(new ApiException("Student does not exist", HttpStatus.NOT_FOUND));

        mockMvc.perform(get("/api/v1/students/" + invalidStudentNumber))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Get existing Student with complete info using valid Auth")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void getStudent_withCompleteDetailsAndUsingValidAuth_returns200OK() throws Exception {
        String validStudentNumber = std0.getStudentNumber();
        StudentDto studentDto = Student.toStudentDto(std0);
        when(service.get(validStudentNumber)).thenReturn(std0);
        String responseJson = mapper.writeValueAsString(studentDto);

        mockMvc.perform(get("/api/v1/students/" + validStudentNumber))
                .andExpect(status().isOk())
                .andExpect(content().json(responseJson));
    }

    @Test
    @DisplayName("Get existing Student with incomplete info using valid Auth")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void getStudent_withIncompleteInfoUsingValidAuth_returns200OK() throws Exception {
        String validStudentNumber = std0.getStudentNumber();
        StudentListDto studentListDto = Student.toStudentListDto(std0);
        when(service.get(validStudentNumber)).thenReturn(std0);
        String responseJson = mapper.writeValueAsString(studentListDto);

        mockMvc.perform(get("/api/v1/students/" + validStudentNumber)
                        .param("complete", "false"))
                .andExpect(status().isOk())
                .andExpect(content().json(responseJson));
    }

    @Test
    @DisplayName("Create Student with invalid Auth")
    void createStudent_withInvalidAuth_returns403Forbidden() throws Exception {
        StudentDto studentDtoRequest = Student.toStudentDto(std0);
        String requestJson = mapper.writeValueAsString(studentDtoRequest);

        mockMvc.perform(post("/api/v1/students")
                .content(requestJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Create Student with valid Auth and malformed Student payload")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void createStudent_withValidAuthAndMalformedStudentPayload_returns400BadRequest() throws Exception {
        std0.setStudentNumber("Invalid Student Number");
        StudentDto studentDtoRequest = Student.toStudentDto(std0);
        String requestJson = mapper.writeValueAsString(studentDtoRequest);

        mockMvc.perform(post("/api/v1/students")
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Create Student with taken Student Number using valid Auth")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void createStudent_withValidAuthAndTakenStudentNumber_returns400BadRequest() throws Exception {
        StudentDto studentDtoRequest = Student.toStudentDto(std0);
        Student studentToCreate = Student.toStudent(studentDtoRequest);
        String requestJson = mapper.writeValueAsString(studentDtoRequest);
        when(service.create(studentToCreate)).thenThrow(new ApiException("Student not found", HttpStatus.BAD_REQUEST));

        mockMvc.perform(post("/api/v1/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Create Student with available Student Number using valid Auth and returns StudentDto")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void createStudent_withValidAuthAndAvailableStudentNumber_returns201CreatedAndNewStudentWithCompleteInfo() throws Exception {
        StudentDto studentDtoRequest = Student.toStudentDto(std0);
        Student studentToCreate = Student.toStudent(studentDtoRequest);
        Student studentCreated = Student.toStudent(studentDtoRequest);
        studentCreated.setId(0L);
        StudentDto studentDto = Student.toStudentDto(studentCreated);
        when(service.create(studentToCreate)).thenReturn(studentCreated);
        String requestJson = mapper.writeValueAsString(studentDtoRequest);
        String responseJson = mapper.writeValueAsString(studentDto);

        mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .param("complete", "true"))
                .andExpect(status().isCreated())
                .andExpect(content().json(responseJson));
    }

    @Test
    @DisplayName("Create Student with available Student Number using valid Auth and returns StudentListDto")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void createStudent_withValidAuthAndAvailableStudentNumber_returns201CreatedAndNewStudentWithIncompleteInfo() throws Exception {
        StudentDto studentDtoRequest = Student.toStudentDto(std0);
        Student studentToCreate = Student.toStudent(studentDtoRequest);
        Student studentCreated = Student.toStudent(studentDtoRequest);
        studentCreated.setId(0L);
        StudentListDto studentListDto = Student.toStudentListDto(studentCreated);
        when(service.create(studentToCreate)).thenReturn(studentCreated);
        String requestJson = mapper.writeValueAsString(studentDtoRequest);
        String responseJson = mapper.writeValueAsString(studentListDto);

        mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .param("complete", "false"))
                .andExpect(status().isCreated())
                .andExpect(content().json(responseJson));
    }

    @Test
    @DisplayName("Update Student using invalid Auth")
    void updateStudent_usingInvalidAuth_returns403Forbidden() throws Exception {
        StudentDto studentDtoRequest = Student.toStudentDto(std0);
        String validStudentNumber = std0.getStudentNumber();
        String requestJson = mapper.writeValueAsString(studentDtoRequest);

        mockMvc.perform(put("/api/v1/students/" + validStudentNumber)
                .content(requestJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Update non existent Student using valid Auth")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void updateStudent_usingValidAuthWithInvalidStudentNumber_returns404NotFound() throws Exception {
        StudentDto studentDtoRequest = Student.toStudentDto(std0);
        Student studentForUpdate = Student.toStudent(studentDtoRequest);
        String invalidStudentNumber = std0.getStudentNumber();
        String requestJson = mapper.writeValueAsString(studentDtoRequest);
        when(service.update(studentForUpdate, invalidStudentNumber)).thenThrow(new ApiException("Student not found", HttpStatus.NOT_FOUND));

        mockMvc.perform(put("/api/v1/students/" + invalidStudentNumber)
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Update existing Student using valid Auth and returns complete Student info")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void updateStudent_usingValidAuthWithValidStudentNumber_returns200OKWithStudentCompleteInfo() throws Exception {
        StudentDto studentDtoRequest = Student.toStudentDto(std0);
        Student studentForUpdate = Student.toStudent(studentDtoRequest);
        Student updatedStudent = Student.toStudent(studentDtoRequest);
        updatedStudent.setId(0L);
        StudentDto studentDtoResponse = Student.toStudentDto(updatedStudent);
        String validStudentNumber = std0.getStudentNumber();
        when(service.update(studentForUpdate, validStudentNumber)).thenReturn(updatedStudent);
        String requestJson = mapper.writeValueAsString(studentDtoRequest);
        String responseJson = mapper.writeValueAsString(studentDtoResponse);

        mockMvc.perform(put("/api/v1/students/" + validStudentNumber)
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("complete", "true"))
                .andExpect(status().isOk())
                .andExpect(content().json(responseJson));
    }

    @Test
    @DisplayName("Update existing Student using valid Auth and returns incomplete Student info")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void updateStudent_usingValidAuthWithValidStudentNumber_returns200OKWithStudentIncompleteInfo() throws Exception {
        StudentDto studentDtoRequest = Student.toStudentDto(std0);
        Student studentForUpdate = Student.toStudent(studentDtoRequest);
        Student updatedStudent = Student.toStudent(studentDtoRequest);
        updatedStudent.setId(0L);
        StudentListDto studentDtoResponse = Student.toStudentListDto(updatedStudent);
        String validStudentNumber = std0.getStudentNumber();
        when(service.update(studentForUpdate, validStudentNumber)).thenReturn(updatedStudent);
        String requestJson = mapper.writeValueAsString(studentDtoRequest);
        String responseJson = mapper.writeValueAsString(studentDtoResponse);

        mockMvc.perform(put("/api/v1/students/" + validStudentNumber)
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("complete", "false"))
                .andExpect(status().isOk())
                .andExpect(content().json(responseJson));
    }

    @Test
    @DisplayName("Delete Student using invalid Auth")
    void deleteStudent_usingInvalidAuth_returns403Forbidden() throws Exception{
        String validStudentNumber = std0.getStudentNumber();

        mockMvc.perform(delete("/api/v1/students/" + validStudentNumber))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Delete non existing Student using valid Auth")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void deleteStudent_usingValidAuthAndInvalidStudentNumber_returns404NotFound() throws Exception {
        String invalidStudentNumber = std0.getStudentNumber();
        doThrow(new ApiException("Student not found", HttpStatus.NOT_FOUND)).when(service).softDelete(invalidStudentNumber);

        mockMvc.perform(delete("/api/v1/students/" + invalidStudentNumber))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Delete existing Student using valid Auth")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void deleteStudent_usingValidAuthAndValidStudentNumber_returns200Ok() throws Exception {
        String validStudentNumber = std0.getStudentNumber();
        doNothing().when(service).softDelete(validStudentNumber);

        mockMvc.perform(delete("/api/v1/students/" + validStudentNumber))
                .andExpect(status().isOk());
    }

    public MockMultipartFile createMultipartFile(List<Student> students, String contentType) {
        try {
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
            for (int i = 0; i < students.size(); i++) {
                Row dataRow = sheet.createRow(i + 1);
                StudentListDto student = Student.toStudentListDto(students.get(i));
                dataRow.createCell(0).setCellValue(student.getId());
                dataRow.createCell(1).setCellValue(student.getStudentNumber());
                dataRow.createCell(2).setCellValue(student.getFullName());
                dataRow.createCell(3).setCellValue(student.getYearAndSection());
                dataRow.createCell(4).setCellValue(student.getContactNumber());
                dataRow.createCell(11).setCellValue(students.get(i).getDeleteFlag());
                dataRow.createCell(12).setCellValue(students.get(i).getProfile());

                // Nullable properties check
                if (student.getBirthday() != null) dataRow.createCell(5).setCellValue(student.getBirthday());
                if (student.getAddress() != null) dataRow.createCell(6).setCellValue(student.getAddress());
                if (student.getEmail() != null) dataRow.createCell(7).setCellValue(student.getEmail());
                if (student.getGuardian() != null) dataRow.createCell(8).setCellValue(student.getGuardian());
                if (student.getGuardianNumber() != null)
                    dataRow.createCell(9).setCellValue(student.getGuardianNumber());
                if (student.getYearLevel() != null) dataRow.createCell(10).setCellValue(student.getYearLevel());

            }

            // Making size of the columns auto resize to fit data
            for (int i = 0; i < columns.size(); i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return new MockMultipartFile("file", "Student.xlsx", contentType, new ByteArrayInputStream(outputStream.toByteArray()));
        } catch (IOException ignored) {
            return null;
        }
    }
}

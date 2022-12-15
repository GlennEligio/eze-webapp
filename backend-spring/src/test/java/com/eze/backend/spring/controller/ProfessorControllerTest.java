package com.eze.backend.spring.controller;

import com.eze.backend.spring.dtos.ProfessorDto;
import com.eze.backend.spring.exception.ApiException;
import com.eze.backend.spring.model.Professor;
import com.eze.backend.spring.service.ProfessorService;
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
import java.util.Locale;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProfessorControllerTest {

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private ProfessorService service;

    private MockMvc mockMvc;
    private ObjectMapper mapper;

    private final static String CORRECT_CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    private static final String INCORRECT_CONTENT_TYPE = "application/vnd.ms-excels";

    private List<Professor> professorList;
    private Professor prof0, prof1;

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
    void setupEach() {
        prof0 = new Professor("Name0", "+639062560574", false, "email0@gmail.com", "https://sampleimage0.com");
        prof1 = new Professor("Name1", "+639062560574", true, "email1@gmail.com", "https://sampleimage1.com");
        professorList = List.of(prof1, prof0);
    }

    @Test
    @DisplayName("Get all Professors using invalid Auth")
    void getProfessors_usingInvalidAuth_returns403Forbidden() throws Exception {
        mockMvc.perform(get("/api/v1/professors"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Get all Professors using valid Auth")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void getProfessors_usingValidAuth_returns200OK() throws Exception {
        List<Professor> profNotDeleted = professorList.stream().filter(p -> !p.getDeleteFlag()).toList();
        List<ProfessorDto> dtoResponse = profNotDeleted.stream().map(Professor::toProfessorDto).toList();
        when(service.getAllNotDeleted()).thenReturn(profNotDeleted);
        String responseJson = mapper.writeValueAsString(dtoResponse);

        mockMvc.perform(get("/api/v1/professors"))
                .andExpect(status().isOk())
                .andExpect(content().json(responseJson));
    }

    @Test
    @DisplayName("Get all Professors with name request param using valid Auth")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void getProfessors_withNameRequestParamUsingValidAuth_returns200OK() throws Exception {
        String nameQuery = "Name1";
        List<Professor> profNotDeleted = professorList.stream().filter(p -> !p.getDeleteFlag()).toList();
        List<Professor> filteredProfessors = profNotDeleted.stream().filter(p -> p.getName().toLowerCase().contains(nameQuery.toLowerCase())).toList();
        List<ProfessorDto> dtoResponse = filteredProfessors.stream().map(Professor::toProfessorDto).toList();
        when(service.getAllNotDeleted()).thenReturn(profNotDeleted);
        String responseJson = mapper.writeValueAsString(dtoResponse);

        mockMvc.perform(get("/api/v1/professors")
                        .param("name", nameQuery))
                .andExpect(status().isOk())
                .andExpect(content().json(responseJson));
    }

    @Test
    @DisplayName("Download Professors using invalid Auth")
    void download_usingInvalidAuth_returns403Forbidden() throws Exception {
        mockMvc.perform(get("/api/v1/professors"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Download Professor using valid Auth")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void download_usingValidAuth_returns200OK() throws Exception {
        prof0.setId(0L);
        prof1.setId(1L);
        MockMultipartFile multipartFile = createMultipartFile(professorList, CORRECT_CONTENT_TYPE);
        when(service.getAll()).thenReturn(professorList);
        when(service.listToExcel(professorList)).thenReturn(new ByteArrayInputStream(multipartFile.getBytes()));

        mockMvc.perform(get("/api/v1/professors/download"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(header().string("Content-Disposition", "attachment; filename=professors.xlsx"));
    }

    @Test
    @DisplayName("Upload Professors using invalid Auth")
    void upload_usingInvalidAuth_returns403Forbidden() throws Exception {
        prof0.setId(0L);
        prof1.setId(1L);
        MockMultipartFile multipartFile = createMultipartFile(professorList, CORRECT_CONTENT_TYPE);

        mockMvc.perform(multipart(HttpMethod.POST, "/api/v1/professors/upload")
                .file(multipartFile))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Upload Professor with invalid file content type using valid Auth")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void upload_withIncorrectContentTypeUsingValidAuth_returns400BadRequest() throws Exception {
        prof0.setId(0L);
        prof1.setId(1L);
        MockMultipartFile multipartFile = createMultipartFile(professorList, INCORRECT_CONTENT_TYPE);

        mockMvc.perform(multipart(HttpMethod.POST, "/api/v1/professors/upload")
                        .file(multipartFile))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Upload Professor with correct file content type using valid Auth")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void upload_withCorrectContentTypeUsingValidAuth_returns200Ok() throws Exception {
        prof0.setId(0L);
        prof1.setId(1L);
        MockMultipartFile multipartFile = createMultipartFile(professorList, CORRECT_CONTENT_TYPE);
        when(service.excelToList(multipartFile)).thenReturn(professorList);
        when(service.addOrUpdate(professorList, false)).thenReturn(0);
        ObjectNode response = mapper.createObjectNode();
        response.put("Professors Affected", 0);
        String responseJson = mapper.writeValueAsString(response);

        mockMvc.perform(multipart(HttpMethod.POST, "/api/v1/professors/upload")
                        .file(multipartFile))
                .andExpect(status().isOk())
                .andExpect(content().json(responseJson));
    }

    @Test
    @DisplayName("Get Professor using invalid Auth")
    void getProfessor_usingInvalidAuth_returns403Forbidden() throws Exception {
        String validName = prof0.getName();

        mockMvc.perform(get("/api/v1/professors/" + validName))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Get non existent Professor using valid Auth")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void getProfessor_withInvalidNameUsingValidAuth_returns404NotFound() throws Exception {
        String invalidName = prof0.getName();
        when(service.get(invalidName)).thenThrow(new ApiException("Professor not found", HttpStatus.NOT_FOUND));

        mockMvc.perform(get("/api/v1/professors/" + invalidName))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Get existing Professor using valid Auth")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void getProfessor_withValidNameUsingValidAuth_returns200Ok() throws Exception {
        String validName = prof0.getName();
        ProfessorDto professorDto = Professor.toProfessorDto(prof0);
        when(service.get(validName)).thenReturn(prof0);
        String responseJson = mapper.writeValueAsString(professorDto);

        mockMvc.perform(get("/api/v1/professors/" + validName))
                .andExpect(status().isOk())
                .andExpect(content().json(responseJson));
    }

    @Test
    @DisplayName("Create Professor using invalid Auth")
    void createProfessor_usingInvalidAuth_returns403Forbidden() throws Exception {
        ProfessorDto professorDto = new ProfessorDto(null, prof0.getName(), prof0.getContactNumber(), prof0.getEmail(), prof0.getProfile());
        String requestJson = mapper.writeValueAsString(professorDto);

        mockMvc.perform(post("/api/v1/professors")
                .content(requestJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Create Professor with taken name using valid Auth")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void createProfessor_withTakenNameUsingValidAuth_returns400BadRequest() throws Exception {
        ProfessorDto professorDto = new ProfessorDto(null, prof0.getName(), prof0.getContactNumber(), prof0.getEmail(), prof0.getProfile());
        Professor professorToCreate = Professor.toProfessor(professorDto);
        when(service.create(professorToCreate)).thenThrow(new ApiException("Professor already exist", HttpStatus.BAD_REQUEST));
        String requestJson = mapper.writeValueAsString(professorDto);

        mockMvc.perform(post("/api/v1/professors")
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Create Professor malformed payload using valid Auth")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void createProfessor_withMalformedPayloadUsingValidAuth_returns400BadRequest() throws Exception {
        ProfessorDto professorDto = new ProfessorDto(null, prof0.getName(), prof0.getContactNumber(), prof0.getEmail(), prof0.getProfile());
        professorDto.setContactNumber("Invalid Contact number format");
        Professor professorToCreate = Professor.toProfessor(professorDto);
        when(service.create(professorToCreate)).thenThrow(new ApiException("Professor already exist", HttpStatus.BAD_REQUEST));
        String requestJson = mapper.writeValueAsString(professorDto);

        mockMvc.perform(post("/api/v1/professors")
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Create Professor with available name using valid Auth")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void createProfessor_withAvailableNameUsingValidAuth_returns201Created() throws Exception {
        ProfessorDto professorDto = new ProfessorDto(null, prof0.getName(), prof0.getContactNumber(), prof0.getEmail(), prof0.getProfile());
        Professor professorToCreate = Professor.toProfessor(professorDto);
        Professor createdProfessor = Professor.toProfessor(professorDto);
        createdProfessor.setId(0L);
        ProfessorDto dtoResponse = Professor.toProfessorDto(createdProfessor);
        when(service.create(professorToCreate)).thenReturn(createdProfessor);
        String requestJson = mapper.writeValueAsString(professorDto);
        String responseJson = mapper.writeValueAsString(dtoResponse);

        mockMvc.perform(post("/api/v1/professors")
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().json(responseJson));
    }

    @Test
    @DisplayName("Update Professor using invalid Auth")
    void updateProfessor_usingInvalidAuth_returns403Forbidden() throws Exception {
        String validName = prof0.getName();
        ProfessorDto dtoRequest = new ProfessorDto(null, prof0.getName(), prof0.getContactNumber(), prof0.getEmail(), prof0.getProfile());
        String requestJson = mapper.writeValueAsString(dtoRequest);

        mockMvc.perform(put("/api/v1/professors/" + validName)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Update Professor with malformed payload using valid Auth")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void updateProfessor_withMalformedPayloadUsingValidAuth_returns400BadRequest() throws Exception {
        String validName = prof0.getName();
        ProfessorDto dtoRequest = new ProfessorDto(null, prof0.getName(), prof0.getContactNumber(), prof0.getEmail(), prof0.getProfile());
        dtoRequest.setContactNumber("INVALID CONTACT NUMBER FORMAT");
        String requestJson = mapper.writeValueAsString(dtoRequest);

        mockMvc.perform(put("/api/v1/professors/" + validName)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Update non-existent Professor payload using valid Auth")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void updateProfessor_withInvalidNameUsingValidAuth_returns404NotFound() throws Exception {
        String invalidName = prof0.getName();
        ProfessorDto dtoRequest = new ProfessorDto(null, prof0.getName(), prof0.getContactNumber(), prof0.getEmail(), prof0.getProfile());
        Professor professorForUpdate = Professor.toProfessor(dtoRequest);
        String requestJson = mapper.writeValueAsString(dtoRequest);
        when(service.update(professorForUpdate, invalidName)).thenThrow(new ApiException("Professor does not exist", HttpStatus.NOT_FOUND));

        mockMvc.perform(put("/api/v1/professors/" + invalidName)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Update existing Professor payload using valid Auth")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void updateProfessor_withValidNameUsingValidAuth_returns200OK() throws Exception {
        String validName = prof0.getName();
        ProfessorDto dtoRequest = new ProfessorDto(null, prof0.getName(), prof0.getContactNumber(), prof0.getEmail(), prof0.getProfile());
        Professor professorForUpdate = Professor.toProfessor(dtoRequest);
        Professor updatedProfessor = Professor.toProfessor(dtoRequest);
        updatedProfessor.setContactNumber("+639062560690");
        ProfessorDto dtoResponse = Professor.toProfessorDto(updatedProfessor);
        when(service.update(professorForUpdate, validName)).thenReturn(updatedProfessor);
        String requestJson = mapper.writeValueAsString(dtoRequest);
        String responseJson = mapper.writeValueAsString(dtoResponse);

        mockMvc.perform(put("/api/v1/professors/" + validName)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().json(responseJson));
    }

    @Test
    @DisplayName("Delete Professor with invalid Auth")
    void deleteProfessor_usingInvalidAuth_returns403Forbidden() throws Exception {
        String validName = prof0.getName();

        mockMvc.perform(delete("/api/v1/professors/" + validName))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Delete non existent Professor with valid Auth")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void deleteProfessor_withInvalidNameUsingValidAuth_returns404NotFound() throws Exception {
        String invalidName = prof0.getName();
        doThrow(new ApiException("Professor does not exist", HttpStatus.NOT_FOUND)).when(service).softDelete(invalidName);

        mockMvc.perform(delete("/api/v1/professors/" + invalidName))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Delete existing Professor with valid Auth")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void deleteProfessor_withValidNameUsingValidAuth_returns200Ok() throws Exception {
        String validName = prof0.getName();
        doNothing().when(service).softDelete(validName);

        mockMvc.perform(delete("/api/v1/professors/" + validName))
                .andExpect(status().isOk());
    }

    public MockMultipartFile createMultipartFile(List<Professor> professors, String contentType) {
        try {
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
            for (int i = 0; i < professors.size(); i++) {
                Row dataRow = sheet.createRow(i + 1);
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
            return new MockMultipartFile("file", "Professors.xlsx", contentType, new ByteArrayInputStream(outputStream.toByteArray()));
        } catch (IOException ignored) {
            return null;
        }
    }
}

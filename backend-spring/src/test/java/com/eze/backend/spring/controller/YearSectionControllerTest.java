package com.eze.backend.spring.controller;

import com.eze.backend.spring.dtos.YearLevelDto;
import com.eze.backend.spring.dtos.YearSectionDto;
import com.eze.backend.spring.dtos.YearSectionWithYearLevelDto;
import com.eze.backend.spring.exception.ApiException;
import com.eze.backend.spring.model.YearLevel;
import com.eze.backend.spring.model.YearSection;
import com.eze.backend.spring.service.YearSectionService;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class YearSectionControllerTest {

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private YearSectionService service;

    private MockMvc mockMvc;
    private ObjectMapper mapper;
    private YearSection ys0, ys1;
    private YearLevel yl;
    private List<YearSection> yearSectionList;

    private final static String CORRECT_CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    private static final String INCORRECT_CONTENT_TYPE = "application/vnd.ms-excels";

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
        yl = new YearLevel(1, "First", false);
        ys0 = new YearSection("SectionName0", false, yl);
        ys1 = new YearSection("SectionName1", true, yl);
        yearSectionList = List.of(ys0, ys1);
    }

    @Test
    @DisplayName("Get all YearSection with valid Auth")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void getYearSections_usingInvalidAuth_returns200OK() throws Exception {
        List<YearSection> yearSectionsNotDeleted = yearSectionList.stream().filter(ys -> !ys.getDeleteFlag()).toList();
        List<YearSectionDto> yearSectionDtos = yearSectionsNotDeleted.stream().map(YearSection::toYearSectionDto).toList();
        when(service.getAllNotDeleted()).thenReturn(yearSectionsNotDeleted);
        String responseJson = mapper.writeValueAsString(yearSectionDtos);

        mockMvc.perform(get("/api/v1/yearSections"))
                .andExpect(status().isOk())
                .andExpect(content().json(responseJson));
    }

    @Test
    @DisplayName("Get all YearSections with invalid Auth")
    void getYearSections_withInvalidAuth_returns403Forbidden() throws Exception {
        mockMvc.perform(get("/api/v1/yearSections"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Download YearSections with invalid Auth")
    void download_withInvalidAuth_returns403Forbidden() throws Exception {
        mockMvc.perform(get("/api/v1/yearSections/download"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Download YearSection with valid Auth")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void download_withValidAuth_returns200OK() throws Exception {
        MockMultipartFile multipartFile = createMultipartFile(yearSectionList, CORRECT_CONTENT_TYPE);
        when(service.getAll()).thenReturn(yearSectionList);
        when(service.listToExcel(yearSectionList)).thenReturn(new ByteArrayInputStream(multipartFile.getBytes()));

        mockMvc.perform(get("/api/v1/yearSections/download"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM));
    }

    @Test
    @DisplayName("Upload YearSection with invalid Auth")
    void upload_withInvalidAuth_returns403Forbidden() throws Exception {
        MockMultipartFile multipartFile = createMultipartFile(yearSectionList, INCORRECT_CONTENT_TYPE);

        mockMvc.perform(multipart(HttpMethod.POST, "/api/v1/yearSections/upload")
                .file(multipartFile))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Upload YearSection with valid Auth using correct Content type")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void upload_withValidAuthAndInvalidContentType_returns200OK() throws Exception {
        MockMultipartFile multipartFile = createMultipartFile(yearSectionList, CORRECT_CONTENT_TYPE);
        when(service.excelToList(multipartFile)).thenReturn(yearSectionList);
        when(service.addOrUpdate(yearSectionList, false)).thenReturn(0);
        ObjectNode response = mapper.createObjectNode();
        response.put("Items Affected", 0);
        String responseJson = mapper.writeValueAsString(response);

        mockMvc.perform(multipart(HttpMethod.POST, "/api/v1/yearSections/upload")
                .file(multipartFile))
                .andExpect(status().isOk())
                .andExpect(content().json(responseJson));
    }

    @Test
    @DisplayName("Upload YearSection with valid Auth using incorrect Content type")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void upload_withValidAuthAndInvalidContentType_returns400BadRequest() throws Exception {
        MockMultipartFile multipartFile = createMultipartFile(yearSectionList, INCORRECT_CONTENT_TYPE);

        mockMvc.perform(multipart(HttpMethod.POST, "/api/v1/yearSections/upload")
                        .file(multipartFile))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Get YearSection using invalid Auth")
    void getYearSection_withInvalidAuth_returns403Forbidden() throws Exception {
        mockMvc.perform(get("/api/v1/yearSections/" + ys0.getSectionName()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Get non existent YearSection using valid Auth")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void getYearSection_withValidAuthAndNonExistentYearSection_returns404NotFound() throws Exception {
        String validSectionName = ys0.getSectionName();
        when(service.get(validSectionName)).thenThrow(new ApiException("YearSection not found", HttpStatus.NOT_FOUND));

        mockMvc.perform(get("/api/v1/sectionName/" + validSectionName))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Get existing YearSection using valid Auth")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void getYearSection_withValidAuthAndExistingYearSection_returns200OK() throws Exception {
        String invalidSectionName = ys0.getSectionName();
        YearSectionWithYearLevelDto yearSectionWithYearLevelDto = YearSection.toYearSectionWithYearLevelDto(ys0);
        when(service.get(invalidSectionName)).thenReturn(ys0);
        String responseJson = mapper.writeValueAsString(yearSectionWithYearLevelDto);

        mockMvc.perform(get("/api/v1/yearSections/" + invalidSectionName))
                .andExpect(status().isOk())
                .andExpect(content().json(responseJson));
    }

    @Test
    @DisplayName("Create YearSection using invalid Auth")
    void createYearSection_withInvalidAuth_returns403Forbidden() throws Exception {
        YearSectionDto yearSectionDto = YearSection.toYearSectionDto(ys0);
        String requestJson = mapper.writeValueAsString(yearSectionDto);

        mockMvc.perform(post("/api/v1/yearSections")
                .content(requestJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Create YearSection using valid Auth and taken SectionName")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void createYearSection_withValidAuthAndTakenSectionName_returns400BadRequest() throws Exception {
        YearLevelDto yearLevelDto = YearLevel.toYearLevelDto(ys0.getYearLevel());
        YearSectionWithYearLevelDto yearSectionWithYearLevelDto = new YearSectionWithYearLevelDto(ys0.getId(), ys0.getSectionName(), yearLevelDto);
        YearSection yearSection = YearSection.toYearSection(yearSectionWithYearLevelDto);
        when(service.create(yearSection)).thenThrow(new ApiException("Section name already taken", HttpStatus.BAD_REQUEST));
        String requestJson = mapper.writeValueAsString(yearSectionWithYearLevelDto);

        mockMvc.perform(post("/api/v1/yearSections")
                .content(requestJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Create YearSection using valid Auth and available SectionName")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void createYearSection_withValidAuthAndAvailableSectionName_returns400BadRequest() throws Exception {
        YearLevelDto yearLevelDto = YearLevel.toYearLevelDto(ys0.getYearLevel());
        YearSectionWithYearLevelDto yearSectionWithYearLevelDto = new YearSectionWithYearLevelDto(ys0.getId(), ys0.getSectionName(), yearLevelDto);
        YearSection yearSectionToCreate = YearSection.toYearSection(yearSectionWithYearLevelDto);
        YearSection newYearSection = YearSection.toYearSection(yearSectionWithYearLevelDto);
        newYearSection.setId(0L);
        YearSectionWithYearLevelDto yearSectionWithYearLevelDtoResponse = YearSection.toYearSectionWithYearLevelDto(newYearSection);
        when(service.create(yearSectionToCreate)).thenReturn(newYearSection);
        String requestJson = mapper.writeValueAsString(yearSectionWithYearLevelDto);
        String responseJson = mapper.writeValueAsString(yearSectionWithYearLevelDtoResponse);

        mockMvc.perform(post("/api/v1/yearSections")
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().json(responseJson));
    }

    @Test
    @DisplayName("Update YearSection using invalid Auth")
    void updateYearSection_withInvalidAuth_returns403Forbidden() throws Exception{
        YearLevelDto yearLevelDto = YearLevel.toYearLevelDto(ys0.getYearLevel());
        YearSectionWithYearLevelDto yearSectionWithYearLevelDtoRequest = new YearSectionWithYearLevelDto(ys0.getId(), ys0.getSectionName(), yearLevelDto);
        String requestJson = mapper.writeValueAsString(yearSectionWithYearLevelDtoRequest);

        mockMvc.perform(put("/api/v1/yearSections/" + ys0.getSectionName())
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Update non existent YearSection using valid Auth")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void updateYearSection_withValidAuthAndInvalidSectionName_returns404NotFound() throws Exception{
        String invalidSectionName = "InvalidSectionName";
        YearLevelDto yearLevelDto = YearLevel.toYearLevelDto(ys0.getYearLevel());
        YearSectionWithYearLevelDto yearSectionWithYearLevelDtoRequest = new YearSectionWithYearLevelDto(ys0.getId(), ys0.getSectionName(), yearLevelDto);
        YearSection yearSectionToUpdate = YearSection.toYearSection(yearSectionWithYearLevelDtoRequest);
        when(service.update(yearSectionToUpdate, invalidSectionName)).thenThrow(new ApiException("YearSection doesn't exist", HttpStatus.NOT_FOUND));
        String requestJson = mapper.writeValueAsString(yearSectionWithYearLevelDtoRequest);

        mockMvc.perform(put("/api/v1/yearSections/" + invalidSectionName)
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Update existing YearSection using valid Auth")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void updateYearSection_withValidAuthAndValidSectionName_returns200OK() throws Exception{
        String validSectionName = ys0.getSectionName();
        YearLevelDto yearLevelDto = YearLevel.toYearLevelDto(ys0.getYearLevel());
        YearSectionWithYearLevelDto yearSectionWithYearLevelDtoRequest = new YearSectionWithYearLevelDto(ys0.getId(), ys0.getSectionName(), yearLevelDto);
        YearSection yearSectionToUpdate = YearSection.toYearSection(yearSectionWithYearLevelDtoRequest);
        YearSection updatedYearSection = YearSection.toYearSection(yearSectionWithYearLevelDtoRequest);
        updatedYearSection.setId(0L);
        YearSectionWithYearLevelDto yearSectionWithYearLevelDtoResponse = YearSection.toYearSectionWithYearLevelDto(updatedYearSection);
        when(service.update(yearSectionToUpdate, validSectionName)).thenReturn(updatedYearSection);
        String requestJson = mapper.writeValueAsString(yearSectionWithYearLevelDtoRequest);
        String responseJson = mapper.writeValueAsString(yearSectionWithYearLevelDtoResponse);

        mockMvc.perform(put("/api/v1/yearSections/" + validSectionName)
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(responseJson));
    }

    @Test
    @DisplayName("Delete YearSection using invalid Auth")
    void deleteYearSection_withInvalidAuth_returns403Forbidden() throws Exception{
        String validSectionName = ys0.getSectionName();

        mockMvc.perform(delete("/api/v1/yearSections/" + validSectionName))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Delete existing YearSection using valid Auth")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void deleteYearSection_withValidAuthAndValidSectionName_returns200OK() throws Exception {
        String validSectionName = ys0.getSectionName();
        doNothing().when(service).softDelete(validSectionName);

        mockMvc.perform(delete("/api/v1/yearSections/" + validSectionName))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Delete non-existent YearSection using valid Auth")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void deleteYearSection_withValidAuthAndInvalidSectionName_returns404NotFound() throws Exception {
        String invalidSectionName = ys0.getSectionName();
        doThrow(new ApiException("YearSection not found", HttpStatus.NOT_FOUND)).when(service).softDelete(invalidSectionName);

        mockMvc.perform(delete("/api/v1/yearSections/" + invalidSectionName))
                .andExpect(status().isNotFound());
    }

    private MockMultipartFile createMultipartFile(List<YearSection> yearSections, String contentType) {
        try {
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
            for (int i = 0; i < yearSections.size(); i++) {
                Row dataRow = sheet.createRow(i + 1);
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
            return new MockMultipartFile("file", "YearSections.xlsx", contentType, new ByteArrayInputStream(outputStream.toByteArray()));
        } catch (IOException ignored) {
            return null;
        }
    }
}

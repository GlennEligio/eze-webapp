package com.eze.backend.spring.controller;

import com.eze.backend.spring.dtos.YearLevelDto;
import com.eze.backend.spring.dtos.YearLevelWithSectionsDto;
import com.eze.backend.spring.exception.ApiException;
import com.eze.backend.spring.model.YearLevel;
import com.eze.backend.spring.model.YearSection;
import com.eze.backend.spring.service.YearLevelService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
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
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class YearLevelControllerTest {

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private YearLevelService service;

    private static final String EXCEL_CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    private MockMvc mockMvc;
    private ObjectMapper mapper;
    private YearLevel yearLevel1, yearLevel2;
    private List<YearLevel> yearLevelList;

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
        yearLevel1 = new YearLevel(1, "First", false);
        yearLevel2 = new YearLevel(2, "Second", true);
        YearSection yearSection0 = new YearSection("SectionName0", false, null);
        YearSection yearSection1 = new YearSection("SectionName1", false, null);
        yearLevel1.setYearSections(new ArrayList<>(List.of(yearSection0)));
        yearLevel2.setYearSections(new ArrayList<>(List.of(yearSection1)));
        yearLevelList = List.of(yearLevel1, yearLevel2);
    }

    @Test
    @DisplayName("Get all YearLevel using invalid Auth")
    void getYearLevels_usingInvalidAuth_returns403Forbidden() throws Exception {
        List<YearLevel> yearLevelsNotDeleted = yearLevelList.stream().filter(yl -> !yl.getDeleteFlag()).toList();
        when(service.getAllNotDeleted()).thenReturn(yearLevelsNotDeleted);

        mockMvc.perform(get("/api/v1/yearLevels"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Get All YearLevels using valid Auth")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void getYearLevels_usingValidAuth_returns200Ok() throws Exception {
        List<YearLevel> yearLevelsNotDeleted = yearLevelList.stream().filter(yl -> !yl.getDeleteFlag()).toList();
        List<YearLevelWithSectionsDto> dtos = yearLevelsNotDeleted.stream().map(YearLevel::toYearLevelWithSectionsDto).toList();
        String json = mapper.writeValueAsString(dtos);
        when(service.getAllNotDeleted()).thenReturn(yearLevelsNotDeleted);

        mockMvc.perform(get("/api/v1/yearLevels"))
                .andExpect(status().isOk())
                .andExpect(content().json(json));
    }

    @Test
    @DisplayName("Download YearLevels.xlsx with valid Auth")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void download_usingValidAuth_returns200Ok() throws Exception {
        MockMultipartFile multipartFile = createMultipartFile(yearLevelList, EXCEL_CONTENT_TYPE);
        when(service.listToExcel(yearLevelList)).thenReturn(new ByteArrayInputStream(multipartFile.getBytes()));
        when(service.getAll()).thenReturn(yearLevelList);

        mockMvc.perform(get("/api/v1/yearLevels/download"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM));
    }

    @Test
    @DisplayName("Download YearLevels.xlsx with invalid Auth")
    void download_usingInvalidAuth_returns403Forbidden() throws Exception {
        mockMvc.perform(get("/api/v1/yearLevels/download"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Upload YearLevels with invalid Auth")
    void upload_usingInvalidAuth_returns403Forbidden() throws Exception {
        mockMvc.perform(post("/api/v1/yearLevels/upload"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Upload YearLevels.xlsx with valid Auth and incorrect file content type")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void upload_usingValidAuthAndIncorrectContentType_return400BadRequest() throws Exception {
        String incorrectFileContentType = "application/vnd.ms-excels";
        MockMultipartFile multipartFile = createMultipartFile(yearLevelList, incorrectFileContentType);

        mockMvc.perform(multipart(HttpMethod.POST, "/api/v1/yearLevels/upload")
                        .file(multipartFile))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Upload YearLevels.xlsx with valid Auth and correct file content type")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void upload_usingValidAuthAndCorrectContentType_returns200Ok() throws Exception {
        MockMultipartFile multipartFile = createMultipartFile(yearLevelList, EXCEL_CONTENT_TYPE);
        when(service.excelToList(multipartFile)).thenReturn(yearLevelList);
        when(service.addOrUpdate(yearLevelList, false)).thenReturn(0);
        ObjectNode response = mapper.createObjectNode();
        response.put("Items Affected", 0);
        String responseJson = mapper.writeValueAsString(response);

        assert multipartFile != null;
        mockMvc.perform(multipart(HttpMethod.POST, "/api/v1/yearLevels/upload")
                        .file(multipartFile))
                .andExpect(status().isOk())
                .andExpect(content().json(responseJson));
    }

    @Test
    @DisplayName("Get YearLevel using invalid Auth")
    void getYearLevel_usingInvalidAuth_returns403Forbidden() throws Exception {
        Integer validYearNumber = yearLevel1.getYearNumber();
        mockMvc.perform(get("/api/v1/yearLevels/" + validYearNumber))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Get non existent YearLevel using valid Auth")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void getYearLevel_usingNonExistentYearLevelAndValidAuth_returns404NotFound() throws Exception {
        Integer invalidYearNumber = yearLevel1.getYearNumber();
        doThrow(new ApiException("YearLevel not found", HttpStatus.NOT_FOUND)).when(service).get(invalidYearNumber);

        mockMvc.perform(get("/api/v1/yearLevels/" + invalidYearNumber))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Get existing YearLevel using valid Auth")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void getYearLevel_usingExistingYearLevelAndValidAuth_returns200OK() throws Exception {
        Integer validYearNumber = yearLevel1.getYearNumber();
        YearLevelWithSectionsDto yearLevelWithSectionsDto = YearLevel.toYearLevelWithSectionsDto(yearLevel1);
        when(service.get(validYearNumber)).thenReturn(yearLevel1);
        String responseJson = mapper.writeValueAsString(yearLevelWithSectionsDto);

        mockMvc.perform(get("/api/v1/yearLevels/" + validYearNumber))
                .andExpect(content().json(responseJson))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Create YearLevel using invalid Auth")
    void createYearLevel_usingInvalidAUth_returns403Forbidden() throws Exception {
        mockMvc.perform(post("/api/v1/yearLevels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(yearLevel1)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Create YearLevel with taken YearNumber using valid Auth")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void createYearLevel_usingValidAuthAndTakenYearNumber_returns400BadRequest() throws Exception {
        YearLevelDto yearLevelDto = new YearLevelDto(yearLevel1.getId(), yearLevel1.getYearNumber(), yearLevel1.getYearName());
        YearLevel yearLevel = YearLevel.toYearLevel(yearLevelDto);
        when(service.create(yearLevel)).thenThrow(new ApiException("YearLevel already exist", HttpStatus.BAD_REQUEST));

        mockMvc.perform(post("/api/v1/yearLevels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(yearLevel1)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Create YearLevel with available YearNumber using valid Auth")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void createYearLevel_usingValidAuthAndAvailableYearNumber_returns201Created() throws Exception {
        YearLevelDto yearLevelDto = new YearLevelDto(yearLevel1.getId(), yearLevel1.getYearNumber(), yearLevel1.getYearName());
        YearLevel yearLevel = YearLevel.toYearLevel(yearLevelDto);
        YearLevel newYearLevel = YearLevel.toYearLevel(yearLevelDto);
        newYearLevel.setYearSections(new ArrayList<>());
        newYearLevel.setId(0L);
        YearLevelDto yearLevelDtoResponse = YearLevel.toYearLevelDto(newYearLevel);

        when(service.create(yearLevel)).thenReturn(newYearLevel);
        String requestJson = mapper.writeValueAsString(yearLevelDto);
        String responseJson = mapper.writeValueAsString(yearLevelDtoResponse);
        log.info(responseJson);

        mockMvc.perform(post("/api/v1/yearLevels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(content().json(responseJson));
    }

    @Test
    @DisplayName("Update YearLevel with invalid Auth")
    void updateYearLevel_usingInvalidAuth_returns403Forbidden() throws Exception {
        YearLevelDto yearLevelDto = YearLevel.toYearLevelDto(yearLevel1);
        String responseJson = mapper.writeValueAsString(yearLevelDto);
        Integer validYearNumber = yearLevel1.getYearNumber();
        mockMvc.perform(put("/api/v1/yearLevels/" + validYearNumber)
                        .content(responseJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Update non existent YearLevel with valid Auth")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void updateYearLevel_usingValidAuthAndInvalidYearNumber_returns404NotFound() throws Exception {
        YearLevelDto yearLevelDto = YearLevel.toYearLevelDto(yearLevel1);
        YearLevel yearLevelForUpdate = YearLevel.toYearLevel(yearLevelDto);
        when(service.update(yearLevelForUpdate, yearLevelForUpdate.getYearNumber())).thenThrow(new ApiException("YearLevel not found", HttpStatus.NOT_FOUND));
        String requestJson = mapper.writeValueAsString(yearLevelDto);

        mockMvc.perform(put("/api/v1/yearLevels/" + yearLevel1.getYearNumber())
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }

    @Test
    @DisplayName("Update existing YearLevel with valid Auth")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void updateYearLevel_usingValidAuthAndValidYearNumber_returns200OK() throws Exception {
        YearLevelDto yearLevelDto = YearLevel.toYearLevelDto(yearLevel1);
        YearLevel yearLevelForUpdate = YearLevel.toYearLevel(yearLevelDto);
        yearLevelForUpdate.setYearSections(new ArrayList<>());
        YearLevelWithSectionsDto yearLevelWithSectionsDto = YearLevel.toYearLevelWithSectionsDto(yearLevelForUpdate);
        when(service.update(yearLevelForUpdate, yearLevelForUpdate.getYearNumber())).thenReturn(yearLevelForUpdate);
        String requestJson = mapper.writeValueAsString(yearLevelDto);
        String responseJson = mapper.writeValueAsString(yearLevelWithSectionsDto);

        mockMvc.perform(put("/api/v1/yearLevels/" + yearLevelDto.getYearNumber())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().json(responseJson));
    }

    @Test
    @DisplayName("Delete YearLevel using invalid Auth")
    void deleteYearLevel_usingInvalidAuth_returns403Forbidden() throws Exception {
        Integer validYearNumber = yearLevel1.getYearNumber();
        mockMvc.perform(delete("/api/v1/yearLevels/" + validYearNumber))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Delete existing YearLevel using valid Auth")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void deleteYearLevel_usingValidAuthAndValidYearNumber_returns200OK() throws Exception {
        Integer validYearNumber = yearLevel1.getYearNumber();
        doNothing().when(service).delete(validYearNumber);

        mockMvc.perform(delete("/api/v1/yearLevels/" + validYearNumber))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Delete non existent YearLevel using invalid Auth")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void deleteYearLevel_usingValidAuthAndInvalidYearNumber_returns404NotFound() throws Exception {
        Integer invalidYearNumber = yearLevel1.getYearNumber();
        doThrow(new ApiException("No Yearlevel exist", HttpStatus.NOT_FOUND)).when(service).softDelete(invalidYearNumber);

        mockMvc.perform(delete("/api/v1/yearLevels/" + invalidYearNumber))
                .andExpect(status().isNotFound());
    }

    private MockMultipartFile createMultipartFile(List<YearLevel> yearLevels, String contentType) {
        try {
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
            for (int i = 0; i < yearLevels.size(); i++) {
                Row dataRow = sheet.createRow(i + 1);
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
            return new MockMultipartFile("file", "YearLevels.xlsx", contentType, new ByteArrayInputStream(outputStream.toByteArray()));
        } catch (IOException ignored) {
            return null;
        }
    }
}

package com.eze.backend.spring.controller;

import com.eze.backend.spring.dtos.EquipmentDto;
import com.eze.backend.spring.enums.EqStatus;
import com.eze.backend.spring.exception.ApiException;
import com.eze.backend.spring.model.Equipment;
import com.eze.backend.spring.model.YearLevel;
import com.eze.backend.spring.service.EquipmentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.With;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EquipmentControllerTest {

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private EquipmentService service;

    private final static String CORRECT_CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    private static final String INCORRECT_CONTENT_TYPE = "application/vnd.ms-excels";

    private MockMvc mockMvc;
    private ObjectMapper mapper;
    private Equipment eq0, eq1;
    private List<Equipment> equipmentList;

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
        eq0 = new Equipment("EqCode0", "Name0", "Barcode0", EqStatus.GOOD, LocalDateTime.now(), true, false, false);
        eq1 = new Equipment("EqCode01", "Name1", "Barcode1", EqStatus.GOOD, LocalDateTime.now(), true, false, true);
        equipmentList = List.of(eq0, eq1);
    }

    @Test
    @DisplayName("Get all Equipments with invalid Auth")
    void getEquipments_withInvalidAuth_returns403Forbidden() throws Exception {
        mockMvc.perform(get("/api/v1/equipments"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Get all Equipments with valid Auth")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void getEquipments_withValidAuth_returns200OK() throws Exception {
        List<Equipment> eqNotDeleted = equipmentList.stream().filter(e -> !e.getDeleteFlag()).toList();
        List<EquipmentDto> equipmentDtos = eqNotDeleted.stream().map(Equipment::toEquipmentDto).toList();
        when(service.getAllNotDeleted()).thenReturn(eqNotDeleted);
        String responseJson = mapper.writeValueAsString(equipmentDtos);
        mockMvc.perform(get("/api/v1/equipments"))
                .andExpect(status().isOk())
                .andExpect(content().json(responseJson));
    }

    @Test
    @DisplayName("Get existing Equipment with invalid Auth")
    void getEquipment_withInvalidAuth_return403Forbidden() throws  Exception {
        String validEqCode = eq0.getEquipmentCode();
        String queryParam = "eqCode";
        mockMvc.perform(get("/api/v1/equipments/" + validEqCode)
                .param("query", queryParam))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Get non existing Equipment using valid Auth and barcode")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void getEquipment_withValidAuthAndInvalidBarcode_returns404NotFound() throws Exception {
        String invalidBarcode = eq0.getBarcode();
        String queryParam = "barcode";
        when(service.getByBarcode(invalidBarcode)).thenThrow(new ApiException("Equipment not found", HttpStatus.NOT_FOUND));

        mockMvc.perform(get("/api/v1/equipments/" + invalidBarcode)
                        .param("query", queryParam))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Get existing Equipment using valid Auth and barcode")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void getEquipment_withValidAuthAndValidBarcode_returns200OK() throws Exception {
        String validBarcode = eq0.getBarcode();
        String queryParam = "barcode";
        EquipmentDto dto = Equipment.toEquipmentDto(eq0);
        String responseJson = mapper.writeValueAsString(dto);
        when(service.getByBarcode(validBarcode)).thenReturn(eq0);

        mockMvc.perform(get("/api/v1/equipments/" + validBarcode)
                        .param("query", queryParam))
                .andExpect(status().isOk())
                .andExpect(content().json(responseJson));
    }

    @Test
    @DisplayName("Get non existing Equipment using valid Auth and equipment code")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void getEquipment_withValidAuthAndInvalidEqCode_returns404NotFound() throws Exception {
        String invalidEqCode = eq0.getEquipmentCode();
        String queryParam = "eqCode";
        when(service.get(invalidEqCode)).thenThrow(new ApiException("Equipment not found", HttpStatus.NOT_FOUND));

        mockMvc.perform(get("/api/v1/equipments/" + invalidEqCode)
                        .param("query", queryParam))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Get existing Equipment using valid Auth and equipment code")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void getEquipment_withValidAuthAndValidEqCode_returns200OK() throws Exception {
        String validEqCode = eq0.getEquipmentCode();
        String queryParam = "eqCode";
        EquipmentDto dto = Equipment.toEquipmentDto(eq0);
        String responseJson = mapper.writeValueAsString(dto);
        when(service.get(validEqCode)).thenReturn(eq0);

        mockMvc.perform(get("/api/v1/equipments/" + validEqCode)
                        .param("query", queryParam))
                .andExpect(status().isOk())
                .andExpect(content().json(responseJson));
    }

    @Test
    @DisplayName("Download Equipments with invalid Auth")
    void download_withInvalidAuth_returns403Forbidden() throws Exception {
        mockMvc.perform(get("/api/v1/equipments/download"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Download Equipments using valid Auth")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void download_withValidAuth_returns200OK() throws Exception {
        eq0.setId(0L);
        eq1.setId(1L);
        MockMultipartFile multipartFile = createMultipartFile(equipmentList, CORRECT_CONTENT_TYPE);
        when(service.getAll()).thenReturn(equipmentList);
        when(service.listToExcel(equipmentList)).thenReturn(new ByteArrayInputStream(multipartFile.getBytes()));

        mockMvc.perform(get("/api/v1/equipments/download"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM));
    }

    @Test
    @DisplayName("Upload Equipments using invalid Auth")
    void upload_withInvalidAuth_returns403Forbidden() throws Exception {
        eq0.setId(0L);
        eq1.setId(1L);
        MockMultipartFile multipartFile = createMultipartFile(equipmentList, CORRECT_CONTENT_TYPE);

        mockMvc.perform(multipart(HttpMethod.POST,"/api/v1/equipments/upload")
                .file(multipartFile))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Upload Equipment using valid Auth and incorrect content type")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void upload_withValidAuthAndIncorrectContentType_returns400BadRequest() throws Exception {
        eq0.setId(0L);
        eq1.setId(1L);
        MockMultipartFile multipartFile = createMultipartFile(equipmentList, INCORRECT_CONTENT_TYPE);

        mockMvc.perform(multipart(HttpMethod.POST, "/api/v1/equipments/upload")
                .file(multipartFile))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Upload Equipments using valid Auth and correct Content type")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void upload_withValidAuthAndCorrectContentType_returns200OK() throws Exception{
        eq0.setId(0L);
        eq1.setId(1L);
        MockMultipartFile multipartFile = createMultipartFile(equipmentList, CORRECT_CONTENT_TYPE);
        when(service.excelToList(multipartFile)).thenReturn(equipmentList);
        when(service.addOrUpdate(equipmentList, false)).thenReturn(0);
        ObjectNode response = mapper.createObjectNode();
        response.put("Items Affected", 0);
        String responseJson = mapper.writeValueAsString(response);

        mockMvc.perform(multipart(HttpMethod.POST, "/api/v1/equipments/upload")
                .file(multipartFile))
                .andExpect(status().isOk())
                .andExpect(content().json(responseJson));
    }

    public MockMultipartFile createMultipartFile(List<Equipment> equipments, String contentType) {
        try {
            XSSFWorkbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Equipments");

            List<String> columns = List.of("ID", "Equipment code", "Name", "Barcode", "Status", "Defective since", "Is Duplicable?", "Is Borrowed?", "Delete Flag");

            // Creating header row
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columns.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns.get(i));
            }

            // Populating the Excel file with data
            for(int i=0; i < equipments.size(); i++) {
                Row dataRow = sheet.createRow(i+1);
                Equipment eq = equipments.get(i);
                dataRow.createCell(0).setCellValue(eq.getId());
                dataRow.createCell(1).setCellValue(eq.getEquipmentCode());
                dataRow.createCell(2).setCellValue(eq.getName());
                dataRow.createCell(3).setCellValue(eq.getBarcode());
                dataRow.createCell(4).setCellValue(eq.getStatus().getName());
                if(eq.getDefectiveSince() != null) {
                    dataRow.createCell(5).setCellValue(eq.getDefectiveSince().toString());
                }
                dataRow.createCell(6).setCellValue(eq.getIsDuplicable());
                dataRow.createCell(7).setCellValue(eq.getIsBorrowed());
                dataRow.createCell(8).setCellValue(eq.getDeleteFlag());
            }

            // Making size of the columns auto resize to fit data
            for(int i=0; i < columns.size(); i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return new MockMultipartFile("file", "Equipments.xlsx", contentType, new ByteArrayInputStream(outputStream.toByteArray()));
        } catch (IOException ignored) {
            return null;
        }
    }
}

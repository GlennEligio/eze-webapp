package com.eze.backend.spring.controller;

import com.eze.backend.spring.dtos.*;
import com.eze.backend.spring.enums.EqStatus;
import com.eze.backend.spring.enums.TxStatus;
import com.eze.backend.spring.exception.ApiException;
import com.eze.backend.spring.model.*;
import com.eze.backend.spring.service.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bson.types.ObjectId;
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
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TransactionControllerTest {

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private TransactionService service;

    private final static String CORRECT_CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    private static final String INCORRECT_CONTENT_TYPE = "application/vnd.ms-excels";

    private MockMvc mockMvc;
    private ObjectMapper mapper;

    private Student student, student2;
    private YearLevel yearLevel;
    private YearSection yearSection;
    private Professor professor;
    private Equipment eq0, eq1, eq2;
    private Transaction tx0, tx1, tx2;
    private LocalDateTime timeStamp, timeStamp2;
    private List<Transaction> transactionList;

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
        timeStamp = LocalDateTime.of(2022, Month.DECEMBER, 21, 12, 45);
        timeStamp2 = LocalDateTime.of(2022, Month.APRIL, 24, 12, 45);
        yearLevel = new YearLevel(1, "First", false);
        yearSection = new YearSection("SectionName1", false, yearLevel);
        student = new Student("2015-00129-MN-01", "FullName1", yearSection, "09062560571", "Birthday1", "Address1", "Email1", "Guardian1", "GuardianNumber1", yearLevel, "https://sampleprofile1.com", false);
        student2 = new Student("2015-00129-MN-02", "FullName2", yearSection, "09062560571", "Birthday2", "Address2", "Email2", "Guardian2", "GuardianNumber2", yearLevel, "https://sampleprofile2.com", false);
        professor = new Professor("Name1", "+639062560574", true);
        eq0 = new Equipment("EqCode0", "Name0", "Barcode0", EqStatus.GOOD, null, false, true, false);
        eq1 = new Equipment("EqCode1", "Name1", "Barcode1", EqStatus.GOOD, null, true, false, false);
        eq2 = new Equipment("EqCode2", "Name2", "Barcode2", EqStatus.GOOD, null, false, false, false);

        // all items returned
        tx0 = new Transaction(new ObjectId().toHexString(), new ArrayList<>(), List.of(eq0, eq1), student, professor, timeStamp, null, TxStatus.PENDING, false);
        // eq0 is not returned yet
        tx1 = new Transaction(new ObjectId().toHexString(), List.of(eq0), List.of(eq0, eq1), student2, professor, timeStamp, null, TxStatus.PENDING, false);
        // eq2 is not returned yet
        tx2 = new Transaction(new ObjectId().toHexString(), List.of(eq2), List.of(eq1, eq2), student2, professor, timeStamp2, null, TxStatus.PENDING, false);
        transactionList = List.of(tx0, tx1, tx2);
    }

    @Test
    @DisplayName("Get all Transaction using invalid Auth")
    void getTransactions_withInvalidAuth_returns403Forbidden() throws Exception {
        mockMvc.perform(get("/api/v1/transactions"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Get all returned Transaction using valid Auth")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void getTransactions_withReturnedParamTrueUsingValidAuth_returns200OK() throws Exception {
        List<Transaction> returnedTranscation = transactionList.stream().filter(t -> t.getEquipments().isEmpty()).toList();
        List<TransactionListDto> transactionListDtosResponse = returnedTranscation.stream().map(Transaction::toTransactionListDto).toList();
        when(service.getAll()).thenReturn(transactionList);
        String completeParam = "false";
        String historicalParam = "false";
        String returnedParam = "true";
        String responseJson = mapper.writeValueAsString(transactionListDtosResponse);

        mockMvc.perform(get("/api/v1/transactions")
                        .param("complete", completeParam)
                        .param("historical", historicalParam)
                        .param("returned", returnedParam))
                .andExpect(status().isOk())
                .andExpect(content().json(responseJson));
    }

    @Test
    @DisplayName("Get all non returned Transaction using valid Auth")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void getTransactions_withReturnedParamFalseUsingValidAuth_returns200OK() throws Exception {
        List<Transaction> unreturnedTranscation = transactionList.stream().filter(t -> !t.getEquipments().isEmpty()).toList();
        List<TransactionListDto> transactionListDtosResponse = unreturnedTranscation.stream().map(Transaction::toTransactionListDto).toList();
        when(service.getAll()).thenReturn(transactionList);
        String completeParam = "false";
        String historicalParam = "false";
        String returnedParam = "false";
        String responseJson = mapper.writeValueAsString(transactionListDtosResponse);

        mockMvc.perform(get("/api/v1/transactions")
                        .param("complete", completeParam)
                        .param("historical", historicalParam)
                        .param("returned", returnedParam))
                .andExpect(status().isOk())
                .andExpect(content().json(responseJson));
    }

    @Test
    @DisplayName("Get all Transaction with from and to date range using valid Auth")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void getTransactions_withFromAndToParamsUsingValidAuth_returns200OKWithTransactionsWithinDateRange() throws Exception {
        LocalDateTime fromDate = LocalDateTime.of(2022, Month.APRIL, 22, 22, 12, 45);
        LocalDateTime toDate = LocalDateTime.of(2022, Month.APRIL, 27, 22, 12, 45);
        List<Transaction> transactionsDateRange = transactionList.stream().filter(t -> t.getBorrowedAt().isAfter(fromDate) && t.getBorrowedAt().isBefore(toDate)).toList();
        List<TransactionListDto> transactionListDtoResponse = transactionsDateRange.stream().map(Transaction::toTransactionListDto).toList();
        when(service.getAll()).thenReturn(transactionList);
        String completeParam = "false";
        String historicalParam = "false";
        String responseJson = mapper.writeValueAsString(transactionListDtoResponse);

        mockMvc.perform(get("/api/v1/transactions")
                        .param("complete", completeParam)
                        .param("historical", historicalParam)
                        .param("fromDate", fromDate.toString())
                        .param("toDate", toDate.toString()))
                .andExpect(status().isOk())
                .andExpect(content().json(responseJson));
    }

    @Test
    @DisplayName("Get all historical Transaction with complete info using valid Auth")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void getTransactions_WithHistoricalTrueAndCompleteTrueUsingValidAuth_returns200OKAndHistoricalAndCompleteTransaction() throws Exception {
        List<TransactionHistDto> transactionListDtoResponse = transactionList.stream().map(Transaction::toTransactionHistDto).toList();
        when(service.getAll()).thenReturn(transactionList);
        String completeParam = "true";
        String historicalParam = "true";
        String responseJson = mapper.writeValueAsString(transactionListDtoResponse);

        mockMvc.perform(get("/api/v1/transactions")
                        .param("complete", completeParam)
                        .param("historical", historicalParam))
                .andExpect(status().isOk())
                .andExpect(content().json(responseJson));
    }

    @Test
    @DisplayName("Get all historical Transaction with incomplete info using valid Auth")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void getTransactions_WithHistoricalTrueAndCompleteFalseUsingValidAuth_returns200OKAndHistoricalAndIncompleteTransaction() throws Exception {
        List<TransactionHistListDto> transactionListDtoResponse = transactionList.stream().map(Transaction::toTransactionHistListDto).toList();
        when(service.getAll()).thenReturn(transactionList);
        String completeParam = "false";
        String historicalParam = "true";
        String responseJson = mapper.writeValueAsString(transactionListDtoResponse);

        mockMvc.perform(get("/api/v1/transactions")
                        .param("complete", completeParam)
                        .param("historical", historicalParam))
                .andExpect(status().isOk())
                .andExpect(content().json(responseJson));
    }

    @Test
    @DisplayName("Get all non-historical Transaction with complete info using valid Auth")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void getTransactions_WithHistoricalFalseAndCompleteTrueUsingValidAuth_returns200OKAndNonHistoricalAndCompleteTransaction() throws Exception {
        List<TransactionDto> transactionListDtoResponse = transactionList.stream().map(Transaction::toTransactionDto).toList();
        when(service.getAll()).thenReturn(transactionList);
        String completeParam = "true";
        String historicalParam = "false";
        String responseJson = mapper.writeValueAsString(transactionListDtoResponse);

        mockMvc.perform(get("/api/v1/transactions")
                        .param("complete", completeParam)
                        .param("historical", historicalParam))
                .andExpect(status().isOk())
                .andExpect(content().json(responseJson));
    }

    @Test
    @DisplayName("Get all non-historical Transaction with incomplete info using valid Auth")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void getTransactions_WithHistoricalFalseAndCompleteFalseUsingValidAuth_returns200OKAndNonHistoricalAndIncompleteTransaction() throws Exception {
        List<TransactionListDto> transactionListDtoResponse = transactionList.stream().map(Transaction::toTransactionListDto).toList();
        when(service.getAll()).thenReturn(transactionList);
        String completeParam = "false";
        String historicalParam = "false";
        String responseJson = mapper.writeValueAsString(transactionListDtoResponse);

        mockMvc.perform(get("/api/v1/transactions")
                        .param("complete", completeParam)
                        .param("historical", historicalParam))
                .andExpect(status().isOk())
                .andExpect(content().json(responseJson));
    }

    @Test
    @DisplayName("Download Transactions using invalid Auth")
    void download_usingInvalidAuth_returns403Forbidden() throws Exception {
        mockMvc.perform(get("/api/v1/transactions/download"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Download Transactions without from and to dates using valid Auth")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void download_usingValidAuthWithoutToAndFromDates_returns200OK() throws Exception {
        MockMultipartFile multipartFile = createMultipartFile(transactionList, CORRECT_CONTENT_TYPE);
        when(service.getAll()).thenReturn(transactionList);
        when(service.listToExcel(transactionList)).thenReturn(new ByteArrayInputStream(multipartFile.getBytes()));

        mockMvc.perform(get("/api/v1/transactions/download"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=transactions.xlsx"));
    }

    @Test
    @DisplayName("Download Transactions with from and to dates using valid Auth")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void download_usingValidAuthWithToAndFromDates_returns200OK() throws Exception {
        // timeStamp variable date is between from and to dates
        LocalDateTime fromDate = LocalDateTime.of(2022, Month.DECEMBER, 20, 12, 45, 45);
        LocalDateTime toDate = LocalDateTime.of(2022, Month.DECEMBER, 23, 12, 45, 45);
        MockMultipartFile multipartFile = createMultipartFile(transactionList, CORRECT_CONTENT_TYPE);
        List<Transaction> transactionsFiltered = transactionList.stream().filter(t -> t.getBorrowedAt().isAfter(fromDate) && t.getBorrowedAt().isBefore(toDate)).toList();
        when(service.getAll()).thenReturn(transactionList);
        when(service.listToExcel(transactionsFiltered)).thenReturn(new ByteArrayInputStream(multipartFile.getBytes()));

        mockMvc.perform(get("/api/v1/transactions/download")
                        .param("fromDate", fromDate.toString())
                        .param("toDate", toDate.toString()))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=transactions(" + fromDate + "-" + toDate + ").xlsx"));
    }

    @Test
    @DisplayName("Upload Transactions using invalid Auth")
    void upload_usingInvalidAuth_returns403Forbidden() throws Exception {
        MockMultipartFile multipartFile = createMultipartFile(transactionList, CORRECT_CONTENT_TYPE);

        mockMvc.perform(multipart(HttpMethod.POST, "/api/v1/transactions/upload")
                .file(multipartFile))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Upload Transactions with incorrect file content type using valid Auth")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void upload_withIncorrectFileContentTypeUsingValidAuth_returns400BadRequest() throws Exception {
        MockMultipartFile multipartFile = createMultipartFile(transactionList, INCORRECT_CONTENT_TYPE);

        mockMvc.perform(multipart(HttpMethod.POST, "/api/v1/transactions/upload")
                .file(multipartFile))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Upload Transactions with correct file content type using valid Auth")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void upload_withCorrectFileContentTypeUsingValidAuth_returns200OK() throws Exception {
        MockMultipartFile multipartFile = createMultipartFile(transactionList, CORRECT_CONTENT_TYPE);
        when(service.excelToList(multipartFile)).thenReturn(transactionList);
        when(service.addOrUpdate(transactionList, false)).thenReturn(0);
        ObjectNode response = mapper.createObjectNode();
        response.put("Items Affected", 0);
        String responseJson = mapper.writeValueAsString(response);

        mockMvc.perform(multipart(HttpMethod.POST, "/api/v1/transactions/upload")
                        .file(multipartFile))
                .andExpect(status().isOk())
                .andExpect(content().json(responseJson));
    }

    @Test
    @DisplayName("Get Transaction using invalid Auth")
    void getTransaction_usingInvalidAuth_returns403Forbidden() throws Exception {
        String validTxCode = tx0.getTxCode();

        mockMvc.perform(get("/api/v1/transactions/" + validTxCode))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Get Transaction with invalid TxCode using valid Auth")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void getTransaction_withInvalidTxCodeUsingValidAuth_returns404NotFound() throws Exception {
        String invalidTxCode = tx0.getTxCode();
        when(service.get(invalidTxCode)).thenThrow(new ApiException("Transaction not found", HttpStatus.NOT_FOUND));

        mockMvc.perform(get("/api/v1/transactions/" + invalidTxCode))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Get historical Transaction with complete equipment info using valid Auth")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void getTransaction_withParamsHistoricalTrueCompleteTrueUsingValidAuth_returns200OkWithHistoricalAndCompleteTransaction() throws Exception {
        String validTxCode = tx0.getTxCode();
        when(service.get(validTxCode)).thenReturn(tx0);
        TransactionHistDto dtoResponse = Transaction.toTransactionHistDto(tx0);
        String responseJson = mapper.writeValueAsString(dtoResponse);
        String complete = "true";
        String historical = "true";

        mockMvc.perform(get("/api/v1/transactions/" + validTxCode)
                        .param("complete", complete)
                        .param("historical", historical))
                .andExpect(status().isOk())
                .andExpect(content().json(responseJson));
    }

    @Test
    @DisplayName("Get historical Transaction with incomplete equipment info using valid Auth")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void getTransaction_withParamsHistoricalTrueCompleteFalseUsingValidAuth_returns200OkWithHistoricalAndIncompleteTransaction() throws Exception {
        String validTxCode = tx0.getTxCode();
        when(service.get(validTxCode)).thenReturn(tx0);
        TransactionHistListDto dtoResponse = Transaction.toTransactionHistListDto(tx0);
        String responseJson = mapper.writeValueAsString(dtoResponse);
        String complete = "false";
        String historical = "true";

        mockMvc.perform(get("/api/v1/transactions/" + validTxCode)
                        .param("complete", complete)
                        .param("historical", historical))
                .andExpect(status().isOk())
                .andExpect(content().json(responseJson));
    }

    @Test
    @DisplayName("Get non-historical Transaction with complete equipment info using valid Auth")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void getTransaction_withParamsHistoricalFalseCompleteTrueUsingValidAuth_returns200OkWithNonHistoricalAndCompleteTransaction() throws Exception {
        String validTxCode = tx0.getTxCode();
        when(service.get(validTxCode)).thenReturn(tx0);
        TransactionDto dtoResponse = Transaction.toTransactionDto(tx0);
        String responseJson = mapper.writeValueAsString(dtoResponse);
        String complete = "true";
        String historical = "false";

        mockMvc.perform(get("/api/v1/transactions/" + validTxCode)
                        .param("complete", complete)
                        .param("historical", historical))
                .andExpect(status().isOk())
                .andExpect(content().json(responseJson));
    }

    @Test
    @DisplayName("Get non-historical Transaction with incomplete equipment info using valid Auth")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void getTransaction_withParamsHistoricalFalseCompleteFalseUsingValidAuth_returns200OkWithNonHistoricalAndIncompleteTransaction() throws Exception {
        String validTxCode = tx0.getTxCode();
        when(service.get(validTxCode)).thenReturn(tx0);
        TransactionListDto dtoResponse = Transaction.toTransactionListDto(tx0);
        String responseJson = mapper.writeValueAsString(dtoResponse);
        String complete = "false";
        String historical = "false";

        mockMvc.perform(get("/api/v1/transactions/" + validTxCode)
                        .param("complete", complete)
                        .param("historical", historical))
                .andExpect(status().isOk())
                .andExpect(content().json(responseJson));
    }

    @Test
    @DisplayName("Create Transaction using invalid Auth")
    void createTransaction_usingInvalidAuth_returns403Forbidden() throws Exception {
        List<Equipment> equipments = List.of(eq0, eq1, eq2);
        List<EquipmentDto> equipmentDtos = equipments.stream().map(Equipment::toEquipmentDto).toList();
        ProfessorDto professorDto = Professor.toProfessorDto(professor);
        StudentDto studentDto = Student.toStudentDto(student);
        CreateUpdateTransactionDto createUpdateTransactionDto = new CreateUpdateTransactionDto(equipmentDtos, studentDto, professorDto, "PENDING");
        String requestJson = mapper.writeValueAsString(createUpdateTransactionDto);

        mockMvc.perform(post("/api/v1/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Create Transaction with malformed payload using valid Auth")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void createTransaction_withMalformedPayloadUsingInvalidAuth_returns400BadRequest() throws Exception {
        List<Equipment> equipments = List.of(eq0, eq1, eq2);
        List<EquipmentDto> equipmentDtos = equipments.stream().map(Equipment::toEquipmentDto).toList();
        ProfessorDto professorDto = Professor.toProfessorDto(professor);
        StudentDto studentDto = Student.toStudentDto(student);
        CreateUpdateTransactionDto createUpdateTransactionDto = new CreateUpdateTransactionDto(equipmentDtos, studentDto, professorDto, "INCORRECT_STATUS");
        String requestJson = mapper.writeValueAsString(createUpdateTransactionDto);

        mockMvc.perform(post("/api/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Create Transaction with taken txCode using valid Auth")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void createTransaction_withTakenTxCodeUsingInvalidAuth_returns400BadRequest() throws Exception {
        List<Equipment> equipments = List.of(eq0, eq1, eq2);
        List<EquipmentDto> equipmentDtos = equipments.stream().map(Equipment::toEquipmentDto).toList();
        ProfessorDto professorDto = Professor.toProfessorDto(professor);
        StudentDto studentDto = Student.toStudentDto(student);
        CreateUpdateTransactionDto createUpdateTransactionDto = new CreateUpdateTransactionDto(equipmentDtos, studentDto, professorDto, "PENDING");
        Transaction transactionToCreate = Transaction.toTransaction(createUpdateTransactionDto);
        when(service.create(transactionToCreate)).thenThrow(new ApiException("Transaction code already exist", HttpStatus.BAD_REQUEST));
        String requestJson = mapper.writeValueAsString(createUpdateTransactionDto);

        mockMvc.perform(post("/api/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Create Transaction with available txCode and complete param true using valid Auth")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void createTransaction_withAvailableTxCodeAndCompleteParamTrueUsingInvalidAuth_returns201CreatedWithCompleteTransaction() throws Exception {
        List<Equipment> equipments = List.of(eq0, eq1, eq2);
        List<EquipmentDto> equipmentDtos = equipments.stream().map(Equipment::toEquipmentDto).toList();
        ProfessorDto professorDto = Professor.toProfessorDto(professor);
        StudentDto studentDto = Student.toStudentDto(student);
        CreateUpdateTransactionDto createUpdateTransactionDto = new CreateUpdateTransactionDto(equipmentDtos, studentDto, professorDto, "PENDING");
        Transaction transactionToCreate = Transaction.toTransaction(createUpdateTransactionDto);
        Transaction createdTransaction = Transaction.toTransaction(createUpdateTransactionDto);
        createdTransaction.setTxCode(tx0.getTxCode());
        TransactionDto dtoResponse = Transaction.toTransactionDto(createdTransaction);
        when(service.create(transactionToCreate)).thenReturn(createdTransaction);
        String requestJson = mapper.writeValueAsString(createUpdateTransactionDto);
        String responseJson = mapper.writeValueAsString(dtoResponse);
        String completeParam = "true";

        mockMvc.perform(post("/api/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .param("complete", completeParam))
                .andExpect(status().isCreated())
                .andExpect(content().json(responseJson));
    }

    @Test
    @DisplayName("Create Transaction with available txCode and complete param false using valid Auth")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void createTransaction_withAvailableTxCodeAndCompleteParamFalseUsingInvalidAuth_returns201CreatedWithIncompleteTransaction() throws Exception {
        List<Equipment> equipments = List.of(eq0, eq1, eq2);
        List<EquipmentDto> equipmentDtos = equipments.stream().map(Equipment::toEquipmentDto).toList();
        ProfessorDto professorDto = Professor.toProfessorDto(professor);
        StudentDto studentDto = Student.toStudentDto(student);
        CreateUpdateTransactionDto createUpdateTransactionDto = new CreateUpdateTransactionDto(equipmentDtos, studentDto, professorDto, "PENDING");
        Transaction transactionToCreate = Transaction.toTransaction(createUpdateTransactionDto);
        Transaction createdTransaction = Transaction.toTransaction(createUpdateTransactionDto);
        createdTransaction.setTxCode(tx0.getTxCode());
        TransactionListDto dtoResponse = Transaction.toTransactionListDto(createdTransaction);
        when(service.create(transactionToCreate)).thenReturn(createdTransaction);
        String requestJson = mapper.writeValueAsString(createUpdateTransactionDto);
        String responseJson = mapper.writeValueAsString(dtoResponse);
        String completeParam = "false";

        mockMvc.perform(post("/api/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .param("complete", completeParam))
                .andExpect(status().isCreated())
                .andExpect(content().json(responseJson));
    }

    @Test
    @DisplayName("Update Transaction using invalid Auth")
    void updateTransaction_usingInvalidAuth_returns403Forbidden() throws Exception {
        String validTxCode = tx0.getTxCode();
        List<Equipment> equipments = List.of(eq0, eq1, eq2);
        List<EquipmentDto> equipmentDtos = equipments.stream().map(Equipment::toEquipmentDto).toList();
        ProfessorDto professorDto = Professor.toProfessorDto(professor);
        StudentDto studentDto = Student.toStudentDto(student);
        CreateUpdateTransactionDto createUpdateTransactionDto = new CreateUpdateTransactionDto(equipmentDtos, studentDto, professorDto, "PENDING");
        String requestJson = mapper.writeValueAsString(createUpdateTransactionDto);

        mockMvc.perform(put("/api/v1/transactions")
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("code", validTxCode))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Update non-existent Transaction using valid Auth")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void updateTransaction_withInvalidTxCodeUsingValidAuth_returns404NotFound() throws Exception {
        String validTxCode = tx0.getTxCode();
        List<Equipment> equipments = List.of(eq0, eq1, eq2);
        List<EquipmentDto> equipmentDtos = equipments.stream().map(Equipment::toEquipmentDto).toList();
        ProfessorDto professorDto = Professor.toProfessorDto(professor);
        StudentDto studentDto = Student.toStudentDto(student);
        CreateUpdateTransactionDto createUpdateTransactionDto = new CreateUpdateTransactionDto(equipmentDtos, studentDto, professorDto, "PENDING");
        Transaction transactionForUpdate = Transaction.toTransaction(createUpdateTransactionDto);
        when(service.update(transactionForUpdate, validTxCode)).thenThrow(new ApiException("Transaction not found", HttpStatus.NOT_FOUND));
        String requestJson = mapper.writeValueAsString(createUpdateTransactionDto);

        mockMvc.perform(put("/api/v1/transactions")
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("code", validTxCode))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Update existing Transaction with malformed payload using valid Auth")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void updateTransaction_withValidTxCodeAndMalformedPayloadUsingValidAuth_returns400BadRequest() throws Exception {
        String validTxCode = tx0.getTxCode();
        List<Equipment> equipments = List.of(eq0, eq1, eq2);
        List<EquipmentDto> equipmentDtos = equipments.stream().map(Equipment::toEquipmentDto).toList();
        ProfessorDto professorDto = Professor.toProfessorDto(professor);
        StudentDto studentDto = Student.toStudentDto(student);
        CreateUpdateTransactionDto createUpdateTransactionDto = new CreateUpdateTransactionDto(equipmentDtos, studentDto, professorDto, "INVALID_STATUS_VALUE");
        String requestJson = mapper.writeValueAsString(createUpdateTransactionDto);

        mockMvc.perform(put("/api/v1/transactions")
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("code", validTxCode))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Update existing Transaction using valid Auth")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void updateTransaction_withValidTxCodeUsingValidAuth_returns200Ok() throws Exception {
        String validTxCode = tx0.getTxCode();
        List<Equipment> equipments = List.of(eq0, eq1, eq2);
        List<EquipmentDto> equipmentDtos = equipments.stream().map(Equipment::toEquipmentDto).toList();
        ProfessorDto professorDto = Professor.toProfessorDto(professor);
        StudentDto studentDto = Student.toStudentDto(student);
        CreateUpdateTransactionDto createUpdateTransactionDto = new CreateUpdateTransactionDto(equipmentDtos, studentDto, professorDto, "PENDING");
        Transaction transactionForUpdate = Transaction.toTransaction(createUpdateTransactionDto);
        Transaction updatedTransaction = Transaction.toTransaction(createUpdateTransactionDto);
        updatedTransaction.setStatus(TxStatus.ACCEPTED);
        TransactionDto dtoResponse = Transaction.toTransactionDto(updatedTransaction);
        when(service.update(transactionForUpdate, validTxCode)).thenReturn(updatedTransaction);
        String requestJson = mapper.writeValueAsString(createUpdateTransactionDto);
        String responseJson = mapper.writeValueAsString(dtoResponse);

        mockMvc.perform(put("/api/v1/transactions")
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("code", validTxCode))
                .andExpect(status().isOk())
                .andExpect(content().json(responseJson));
    }

    @Test
    @DisplayName("Deleting Transaction using invalid Auth")
    void deleteTransaction_usingInvalidAuth_returns403Forbidden() throws Exception {
        String validTxCode = tx0.getTxCode();

        mockMvc.perform(delete("/api/v1/transactions/" + validTxCode))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Deleting non existent Transaction using valid Auth")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void deleteTransaction_withInvalidTxCodeUsingValidAuth_returns404NotFound() throws Exception {
        String invalidTxCode = tx0.getTxCode();
        doThrow(new ApiException("Transaction not found", HttpStatus.NOT_FOUND)).when(service).softDelete(invalidTxCode);

        mockMvc.perform(delete("/api/v1/transactions/" + invalidTxCode))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deleting existing Transaction using valid Auth")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void deleteTransaction_withValidTxCodeUsingValidAuth_returns200Ok() throws Exception {
        String validTxCode = tx0.getTxCode();
        doNothing().when(service).softDelete(validTxCode);

        mockMvc.perform(delete("/api/v1/transactions/" + validTxCode))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Returning equipments using invalid Auth")
    void returnEquipments_usingInvalidAuth_returns403Forbidden() throws Exception {
        String borrowerParam = student.getStudentNumber();
        String professorParam = professor.getName();
        String[] barcodesParam = new String[]{eq0.getBarcode()};

        mockMvc.perform(put("/api/v1/transactions/return")
                .param("borrower", borrowerParam)
                .param("professor", professorParam)
                .param("barcode", barcodesParam))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Returning equipments with no transaction match using valid Auth")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void returnEquipments_withNoTransactionMatchUsingValidAuth_returns404NotFound() throws Exception {
        String borrowerParam = student.getStudentNumber();
        String professorParam = professor.getName();
        String[] barcodes = new String[]{eq0.getBarcode()};
        String barcodeParam = String.join(",", barcodes);
        when(service.returnEquipments(borrowerParam, professorParam, Arrays.asList(barcodes))).thenThrow(new ApiException("No transaction match found", HttpStatus.NOT_FOUND));

        mockMvc.perform(put("/api/v1/transactions/return")
                        .param("borrower", borrowerParam)
                        .param("professor", professorParam)
                        .param("barcodes", barcodeParam))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Returning equipments with transaction match using valid Auth")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void returnEquipments_withTransactionMatchUsingValidAuth_returns200OkWithUpdatedTransaction() throws Exception {
        String txCode = new ObjectId().toHexString();
        String borrowerParam = student.getStudentNumber();
        String professorParam = professor.getName();
        String[] barcodes = new String[]{eq0.getBarcode()};
        String barcodesParam = String.join(",", barcodes);
        tx1.setTxCode(txCode);
        Transaction updatedTx1 = new Transaction(txCode, new ArrayList<>(), List.of(eq0, eq1), student2, professor, timeStamp, null, TxStatus.PENDING, false);
        TransactionListDto transactionListDto = Transaction.toTransactionListDto(updatedTx1);
        when(service.returnEquipments(borrowerParam, professorParam, Arrays.asList(barcodes))).thenReturn(updatedTx1);
        String responseJson = mapper.writeValueAsString(transactionListDto);

        mockMvc.perform(put("/api/v1/transactions/return")
                        .param("borrower", borrowerParam)
                        .param("professor", professorParam)
                        .param("barcodes", barcodesParam))
                .andExpect(status().isOk())
                .andExpect(content().json(responseJson));
    }


    public MockMultipartFile createMultipartFile(List<Transaction> transactions, String contentType) {
        try {
            // remove duplicable eqs and returned equipments (isBorrowed is false)
            tx0.setEquipments(new ArrayList<>());
            tx0.setEquipmentsHist(List.of(eq0, eq1));
            tx1.setEquipments(new ArrayList<>());
            tx1.setEquipmentsHist(List.of(eq0, eq1));
            List<Transaction> transactionsExpected = List.of(tx0, tx1);
            XSSFWorkbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Transactions");

            List<String> columns = List.of("Transaction Code", "Equipment", "Borrower", "Year and Section", "Professor", "Borrowed At", "Returned At", "Status", "Is Returned", "Delete flag");

            // Creating header row
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columns.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns.get(i));
            }

            // Populating the Excel file with data
            int counter = 0;
            for (Transaction transaction : transactionsExpected) {
                // Create transaction data row per equipment, will cause redundancy
                for (int j = 0; j < transaction.getEquipmentsHist().size(); j++) {
                    Equipment equipmentHist = transaction.getEquipmentsHist().get(j);
                    Row dataRow = sheet.createRow(counter + 1);

                    dataRow.createCell(0).setCellValue(transaction.getTxCode());
                    dataRow.createCell(1).setCellValue(equipmentHist.getEquipmentCode());
                    dataRow.createCell(2).setCellValue(transaction.getBorrower().getStudentNumber());
                    dataRow.createCell(3).setCellValue(transaction.getBorrower().getYearAndSection().getSectionName());
                    dataRow.createCell(4).setCellValue(transaction.getProfessor().getName());
                    dataRow.createCell(5).setCellValue(transaction.getBorrowedAt().toString());
                    if (transaction.getReturnedAt() != null) {
                        dataRow.createCell(6).setCellValue(transaction.getReturnedAt().toString());
                    }
                    dataRow.createCell(7).setCellValue(transaction.getStatus().getName());

                    // Checks if the equipment is still in current Equipment list of Transaction
                    dataRow.createCell(8).setCellValue(!transaction.getEquipments().contains(equipmentHist));
                    dataRow.createCell(9).setCellValue(transaction.getDeleteFlag());
                    counter++;
                }
            }

            // Making size of the columns auto resize to fit data
            for (int i = 0; i < columns.size(); i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return new MockMultipartFile("file", "Transactions.xlsx", contentType, new ByteArrayInputStream(outputStream.toByteArray()));

        } catch (IOException ignored) {
            return null;
        }
    }
}

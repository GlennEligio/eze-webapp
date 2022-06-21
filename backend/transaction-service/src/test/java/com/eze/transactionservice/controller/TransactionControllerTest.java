package com.eze.transactionservice.controller;

import static org.mockito.Mockito.*;

import com.eze.transactionservice.domain.Item;
import com.eze.transactionservice.domain.Status;
import com.eze.transactionservice.domain.Transaction;
import com.eze.transactionservice.domain.TransactionItem;
import com.eze.transactionservice.exception.ApiException;
import com.eze.transactionservice.service.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.MultiValueMap;
import org.springframework.util.MultiValueMapAdapter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@WebMvcTest(controllers = TransactionController.class)
public class TransactionControllerTest {

    @MockBean
    private TransactionService service;

    @Autowired
    private MockMvc mockMvc;

    private Transaction transaction0;
    private List<Transaction> transactions;
    private static MultiValueMap<String, String> headers;
    private static ObjectMapper mapper;
    private static String BASE_URI;

    @BeforeAll
    static void setupAll() {
        headers = new MultiValueMapAdapter<>(new HashMap<>());
        headers.add("X-auth-username", "randomUsername");
        headers.add("X-auth-role", "ROLE_ADMIN");
        mapper = new ObjectMapper()
                .setAnnotationIntrospector(new JacksonAnnotationIntrospector())
                .registerModule(new JavaTimeModule())
                .setDateFormat(new StdDateFormat())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        BASE_URI = "/api/v1/transactions";
    }

    @BeforeEach
    void setup() {
        Item item0 = new Item("itemCode0");
        Item item1 = new Item("itemCode1");
        TransactionItem transItem0 = new TransactionItem(1L, item0);
        TransactionItem transItem1 = new TransactionItem(2L, item1);
        transaction0 = new Transaction("transactionId0",
                List.of(transItem1, transItem0),
                "accepter0",
                "requester0",
                Status.PENDING,
                LocalDateTime.now(),
                LocalDateTime.now(),
                false);
        Transaction transaction1 = new Transaction("transactionId1",
                List.of(transItem1, transItem0),
                "accepter1",
                "requester1",
                Status.PENDING,
                LocalDateTime.now(),
                LocalDateTime.now(),
                false);
        Transaction transaction2 = new Transaction("transactionId2",
                List.of(transItem1, transItem0),
                "accepter2",
                "requester2",
                Status.PENDING,
                LocalDateTime.now(),
                LocalDateTime.now(),
                false);
        transactions = List.of(transaction0, transaction1, transaction2);
    }

    @DisplayName("get all Transactions and return 200 OK with Transactions")
    @Test
    @WithMockUser(roles = "USER")
    void getAllTransactions_withTransactionsPresent_returnOk() throws Exception {
        when(service.findAllTransactions()).thenReturn(transactions);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URI)
                        .headers(HttpHeaders.readOnlyHttpHeaders(headers)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(transactions)));
    }

    @DisplayName("get Transaction with valid TransactionId and return 200 OK with Transaction")
    @Test
    @WithMockUser(roles = "USER")
    void getTransaction_withValidTransactionId_returnOk() throws Exception {
        String validTxId = transaction0.getTransactionCode();
        when(service.findTransaction(validTxId)).thenReturn(transaction0);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URI + "/" + validTxId)
                        .headers(HttpHeaders.readOnlyHttpHeaders(headers)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(transaction0)));
    }

    @DisplayName("get Transaction with invalid TransactionId and return 404 NOT FOUND")
    @Test
    @WithMockUser(roles = "USER")
    void getTransaction_withInvalidTransactionId_returnNotFound() throws Exception {
        String invalidTxId = transaction0.getTransactionCode();
        when(service.findTransaction(invalidTxId)).thenThrow(new ApiException("Transaction not found", HttpStatus.NOT_FOUND));

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URI + "/" + invalidTxId)
                        .headers(HttpHeaders.readOnlyHttpHeaders(headers)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @DisplayName("create new Transaction using User account and returns 403 FORBIDDEN")
    @Test
    @WithMockUser(roles = "USER")
    void createTransaction_withUserAccount_returnsForbidden() throws Exception {
        when(service.createTransaction(transaction0)).thenReturn(transaction0);

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URI)
                        .headers(HttpHeaders.readOnlyHttpHeaders(headers))
                        .content(mapper.writeValueAsString(transaction0))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @DisplayName("create new Transaction using NOT User account and returns 201 CREATED with new Transaction")
    @Test
    @WithMockUser(roles = "ADMIN")
    void createTransaction_withNewTransaction_returnsCreated() throws Exception {
        when(service.createTransaction(transaction0)).thenReturn(transaction0);

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URI)
                        .headers(HttpHeaders.readOnlyHttpHeaders(headers))
                        .content(mapper.writeValueAsString(transaction0))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(transaction0)));
    }

    @DisplayName("create existing Transaction using NOT User account and returns 400 BAD REQUEST")
    @Test
    @WithMockUser(roles = "ADMIN")
    void createTransaction_withExistingTransaction_returnsBadRequest() throws Exception {
        when(service.createTransaction(transaction0)).thenThrow(new ApiException("Transaction already exist", HttpStatus.BAD_REQUEST));

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URI)
                        .headers(HttpHeaders.readOnlyHttpHeaders(headers))
                        .content(mapper.writeValueAsString(transaction0))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @DisplayName("update existing Transaction using User account and returns 403 FORBIDDEN")
    @Test
    @WithMockUser(roles = "USER")
    void updateTransaction_withUserAccount_returnsForbidden() throws Exception {
        when(service.updateTransaction(transaction0)).thenReturn(transaction0);

        mockMvc.perform(MockMvcRequestBuilders.put(BASE_URI)
                        .headers(HttpHeaders.readOnlyHttpHeaders(headers))
                        .content(mapper.writeValueAsString(transaction0))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @DisplayName("update existing Transaction using NOT User account and returns 200 OK")
    @Test
    @WithMockUser(roles = "ADMIN")
    void updateTransaction_withExistingTransaction_returnsOk() throws Exception {
        when(service.updateTransaction(transaction0)).thenReturn(transaction0);

        mockMvc.perform(MockMvcRequestBuilders.put(BASE_URI)
                        .headers(HttpHeaders.readOnlyHttpHeaders(headers))
                        .content(mapper.writeValueAsString(transaction0))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @DisplayName("update non-existing Transaction using NOT User account and returns 404 NOT FOUND")
    @Test
    @WithMockUser(roles = "ADMIN")
    void updateTransaction_withNonExistingTransaction_returnsNotFound() throws Exception {
        when(service.updateTransaction(transaction0)).thenThrow(new ApiException("Transaction not found", HttpStatus.NOT_FOUND));

        mockMvc.perform(MockMvcRequestBuilders.put(BASE_URI)
                        .headers(HttpHeaders.readOnlyHttpHeaders(headers))
                        .content(mapper.writeValueAsString(transaction0))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @DisplayName("delete existing Transaction using User account and returns 403 FORBIDDEN")
    @Test
    @WithMockUser(roles = "USER")
    void deleteTransaction_withUserAccount_returnsForbidden() throws Exception {
        when(service.deleteTransaction(transaction0.getTransactionCode())).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URI + "/" + transaction0.getTransactionCode())
                        .headers(HttpHeaders.readOnlyHttpHeaders(headers)))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @DisplayName("delete existing Transaction using NOT User account and returns 200 OK")
    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteTransaction_withExistingTransaction_returnsOk() throws Exception {
        when(service.deleteTransaction(transaction0.getTransactionCode())).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URI + "/" + transaction0.getTransactionCode())
                        .headers(HttpHeaders.readOnlyHttpHeaders(headers)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @DisplayName("delete non-existing Transaction using NOT User account and returns 404 NOT FOUND")
    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteTransaction_withNonExistingTransaction_returnsNotFound() throws Exception {
        when(service.deleteTransaction(transaction0.getTransactionCode())).thenThrow(new ApiException("Transaction not found", HttpStatus.NOT_FOUND));

        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URI + "/" + transaction0.getTransactionCode())
                        .headers(HttpHeaders.readOnlyHttpHeaders(headers)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @DisplayName("fetch professor Transaction using valid prof Id and status and returns 200 OK with transactions")
    @Test
    @WithMockUser(roles = "USER")
    void getTransactionProfessor_withValidProfIdAndStatus_returnsOkWithTransactions() throws Exception {
        String validProfId = transaction0.getAcceptedBy();
        String validStatus = transaction0.getStatus().getStatusName();
        List<Transaction> expectedTransactions = List.of(transaction0);
        when(service.findProfessorTransactions(validProfId, validStatus)).thenReturn(expectedTransactions);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URI + "/professor/" + validProfId + "/" + validStatus)
                .headers(HttpHeaders.readOnlyHttpHeaders(headers)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(expectedTransactions)));
    }

    @DisplayName("fetch professor Transaction using invalid status and returns 400 BAD REQUEST")
    @Test
    @WithMockUser(roles = "USER")
    void getTransactionProfessor_withInvalid_returnsBadRequest() throws Exception {
        String validProfId = transaction0.getAcceptedBy();
        String invalidStatus = "invalidStatus";
        when(service.findProfessorTransactions(validProfId, invalidStatus)).thenThrow(new ApiException("Wrong status name used, can only use [pending, denied, accepted]", HttpStatus.BAD_REQUEST));

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URI + "/professor/" + validProfId + "/" + invalidStatus)
                        .headers(HttpHeaders.readOnlyHttpHeaders(headers)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @DisplayName("fetch student Transaction using valid student Id and status and returns 200 OK with transactions")
    @Test
    @WithMockUser(roles = "USER")
    void getTransactionStudent_withValidStudentIdAndStatus_returnsOkWithTransactions() throws Exception {
        String validStudentId = transaction0.getRequestedBy();
        String validStatus = transaction0.getStatus().getStatusName();
        List<Transaction> expectedTransactions = List.of(transaction0);
        when(service.findStudentTransactions(validStudentId, validStatus)).thenReturn(expectedTransactions);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URI + "/student/" + validStudentId + "/" + validStatus)
                        .headers(HttpHeaders.readOnlyHttpHeaders(headers)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(expectedTransactions)));
    }

    @DisplayName("fetch student Transaction using invalid status and returns 400 BAD REQUEST")
    @Test
    @WithMockUser(roles = "USER")
    void getTransactionStudent_withInvalid_returnsBadRequest() throws Exception {
        String validStudentId = transaction0.getRequestedBy();
        String invalidStatus = "invalidStatus";
        when(service.findStudentTransactions(validStudentId, invalidStatus)).thenThrow(new ApiException("Wrong status name used, can only use [pending, denied, accepted]", HttpStatus.BAD_REQUEST));

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URI + "/student/" + validStudentId + "/" + invalidStatus)
                        .headers(HttpHeaders.readOnlyHttpHeaders(headers)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}

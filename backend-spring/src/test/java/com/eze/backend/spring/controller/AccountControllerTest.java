package com.eze.backend.spring.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.eze.backend.spring.dtos.EzeUserDetails;
import com.eze.backend.spring.dtos.LoginRequestDto;
import com.eze.backend.spring.dtos.LoginResponseDto;
import com.eze.backend.spring.dtos.RegisterRequestDto;
import com.eze.backend.spring.enums.AccountType;
import com.eze.backend.spring.exception.ApiException;
import com.eze.backend.spring.model.Account;
import com.eze.backend.spring.service.AccountService;
import com.eze.backend.spring.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.With;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContext;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AccountControllerTest {

    @Autowired
    private WebApplicationContext context;
    private MockMvc mockMvc;

    @MockBean
    private AccountService service;
    @MockBean
    private BCryptPasswordEncoder passwordEncoder;
    @MockBean
    private JwtUtil jwtUtil;

    private ObjectMapper mapper;
    private LoginRequestDto loginRequestDto;
    private EzeUserDetails userDetails;
    private Account account0, account1;
    private List<Account> accountList;
    private MockMultipartFile multipartFile;

    @BeforeEach
    void setup() {
        loginRequestDto = new LoginRequestDto("Username0", "Password0");
        account0 = new Account("Name0", "Username0", "Email0", "Password0", AccountType.SA, "http://sampleurl.com/profile0", LocalDateTime.now(), true, false);
        account0.setId(0L);
        account1 = new Account("Name1", "Username1", "Email1", "Password1", AccountType.SA, "http://sampleurl.com/profile1", LocalDateTime.now(), true, false);
        account1.setId(1L);
        userDetails = new EzeUserDetails(account0);
        mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
        accountList = List.of(account1, account0);
    }

    @Test
    @DisplayName("Should create MockMvc")
    void shouldCreateMockMvc() {
        assertNotNull(mockMvc);
    }

    @Test
    @DisplayName("Login using invalid credentials")
    @WithAnonymousUser
    void login_usingInvalidCredentials_returns401Unauthorized() throws Exception {
        when(service.loadUserByUsername(loginRequestDto.getUsername())).thenReturn(userDetails);
        when(passwordEncoder.matches(loginRequestDto.getPassword(), userDetails.getPassword())).thenReturn(false);
        String json = mapper.writeValueAsString(loginRequestDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/accounts/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    @DisplayName("Login using valid credentials")
    @WithAnonymousUser
    void login_usingValidCredentials_returns200OkWithLoginResponse() throws Exception {
        when(service.loadUserByUsername(loginRequestDto.getUsername())).thenReturn(userDetails);
        when(passwordEncoder.matches(loginRequestDto.getPassword(), userDetails.getPassword())).thenReturn(true);
        String accessToken = "AccessToken0";
        String refreshToken = "RefreshToken0";
        when(jwtUtil.generateRefreshToken(userDetails)).thenReturn(refreshToken);
        when(jwtUtil.generateToken(userDetails)).thenReturn(accessToken);
        LoginResponseDto responseDto = new LoginResponseDto(userDetails.getUsername(), userDetails.getAuthorities().stream().findFirst().get().toString(), userDetails.getFullName(), accessToken, refreshToken, userDetails.getProfile());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/accounts/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(loginRequestDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(responseDto)));
    }

    @Test
    @DisplayName("Register User")
    @WithAnonymousUser
    void register_usingValidRegisterData_returns200OKWithAccessTokens() throws Exception {
        RegisterRequestDto registerRequestDto = new RegisterRequestDto(account0.getUsername(), account0.getPassword(), account0.getFullName(), account0.getFullName(), account0.getProfile());
        Account accountToCreate = registerRequestDto.createAccount();
        String accessToken = "AccessToken0";
        String refreshToken = "RefreshToken0";
        LoginResponseDto responseDto = new LoginResponseDto(userDetails.getUsername(), userDetails.getAuthorities().stream().findFirst().get().toString(), userDetails.getFullName(), accessToken, refreshToken, userDetails.getProfile());
        when(service.loadUserByUsername(account0.getUsername())).thenReturn(userDetails);
        when(service.create(accountToCreate)).thenReturn(account0);
        when(jwtUtil.generateRefreshToken(userDetails)).thenReturn(refreshToken);
        when(jwtUtil.generateToken(userDetails)).thenReturn(accessToken);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/accounts/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(registerRequestDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(responseDto)));
    }

    @Test
    @DisplayName("Upload with invalid file (not .xslx)")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void upload_usingInvalidFileFormat_returns400BADREQUEST() throws Exception {
        String invalidContentType = "application/vnd.ms-excel";
        multipartFile = createDummyMultipartFile(accountList, invalidContentType);

        mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, "/api/v1/accounts/upload")
                .file(multipartFile)
                .contentType(invalidContentType))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @DisplayName("Upload with correct file format")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void upload_usingValidFileFormat_returns200OKWithItemsAffected() throws Exception {
        ObjectNode node = mapper.createObjectNode();
        node.put("Items Affected", 0);
        String itemsAffectedJson = mapper.writeValueAsString(node);
        String validContentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        multipartFile = createDummyMultipartFile(accountList, validContentType);
        when(service.excelToList(multipartFile)).thenReturn(accountList);
        when(service.addOrUpdate(accountList, false)).thenReturn(0);

        mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, "/api/v1/accounts/upload")
                        .file(multipartFile))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(itemsAffectedJson));
    }

    @Test
    @DisplayName("Download Account excel file")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void download_usingValidAuthority_returns200OKWithFile() throws Exception {
        List<Account> accountsWithNoSA = accountList.stream().filter(a -> !a.getType().equals(AccountType.SADMIN)).toList();
        multipartFile = createDummyMultipartFile(accountsWithNoSA, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        when(service.getAll()).thenReturn(accountList);
        when(service.listToExcel(accountsWithNoSA)).thenReturn(new ByteArrayInputStream(multipartFile.getBytes()));

        mockMvc.perform(get("/api/v1/accounts/download"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM));
    }

    @Test
    @DisplayName("Download Account excel file with invalid authority")
    @WithAnonymousUser
    void download_usingInvalidAuthority_returns200OKWithFile() throws Exception {
        List<Account> accountsWithNoSA = accountList.stream().filter(a -> !a.getType().equals(AccountType.SADMIN)).toList();
        multipartFile = createDummyMultipartFile(accountsWithNoSA, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        when(service.getAll()).thenReturn(accountList);
        when(service.listToExcel(accountsWithNoSA)).thenReturn(new ByteArrayInputStream(multipartFile.getBytes()));

        mockMvc.perform(get("/api/v1/accounts/download"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Get all Accounts using Authenticated User")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void getAccounts_withAuthenticatedUser_returnsAccounts() throws Exception {
        List<Account> accountsWithNoSA = accountList.stream().filter(a -> !a.getType().equals(AccountType.SADMIN)).toList();
        String accountsJson = mapper.writeValueAsString(accountsWithNoSA);
        log.info(accountsJson);
        when(service.getAllNotDeleted()).thenReturn(accountsWithNoSA);

        mockMvc.perform(get("/api/v1/accounts"))
                .andExpect(status().isOk())
                .andExpect(content().json(accountsJson));
    }

    @Test
    @DisplayName("Get Account using invalid Authority")
    void getAccount_withNoAuthenticatedUser_returns403Forbidden() throws Exception {
        String accountUsername = account0.getUsername();
        mockMvc.perform(get("/api/v1/accounts/{}", accountUsername))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Get non-existent Account using valid Authority")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void getAccount_withValidAuthorityAndNonExistentAccount_returns404NotFound() throws Exception {
        String accountUsername = account0.getUsername();
        when(service.get(accountUsername)).thenThrow(new ApiException("No account was found", HttpStatus.NOT_FOUND));

        mockMvc.perform(get("/api/v1/accounts/" + accountUsername))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Create Account with invalid Authority")
    void createAccount_withInvalidAuthority_returns403Forbidden() throws Exception {
        String json = mapper.writeValueAsString(account0);
        when(service.create(account0)).thenReturn(account0);

        mockMvc.perform(post("/api/v1/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Create Account with valid Authority and malformed Account payload")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void createAccount_withValidAuthAndMalformedPayload_returns400BadRequest() throws Exception {
        account0.setProfile("Invalid Profile image url");
        String json = mapper.writeValueAsString(account0);
        when(service.create(account0)).thenReturn(account0);

        mockMvc.perform(post("/api/v1/accounts")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Create Account with valid Authority and proper Account payload")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void createAccount_withValidAuthAndCorrectPayload_returns201Created() throws Exception {
        String json = mapper.writeValueAsString(account0);
        when(service.create(account0)).thenReturn(account0);

        mockMvc.perform(post("/api/v1/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(content().json(json));
    }

    @Test
    @DisplayName("Update Account with invalid Authority")
    void updateAccount_withInvalidAuth_returns403Forbidden() throws Exception {
        String json = mapper.writeValueAsString(account0);
        String username = account0.getUsername();
        when(service.update(account0, username)).thenReturn(account0);

        mockMvc.perform(put("/api/v1/accounts/" + username)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Update Account with valid Authority and malformed payload")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void updateAccount_withValidAuthAndMalformedPayload_returns401BadRequest() throws Exception {
        account0.setProfile("Invalid Profile url");
        String json = mapper.writeValueAsString(account0);
        String username = account0.getUsername();
        when(service.update(account0, username)).thenReturn(account0);

        mockMvc.perform(put("/api/v1/accounts/" + username)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Update Account with valid Auth and proper payload")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void updateAccount_withValidAuthAndProperPayload_returns200OK() throws Exception {
        String username = account0.getUsername();
        String json = mapper.writeValueAsString(account0);
        when(service.update(account0, username)).thenReturn(account0);

        mockMvc.perform(put("/api/v1/accounts/" + username)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(content().json(json));
    }

    @Test
    @DisplayName("Delete Account with invalid Auth")
    void deleteAccount_withInvalidAuth_returns403Forbidden() throws Exception {
        String username = account0.getUsername();

        mockMvc.perform(delete("/api/v1/accounts/" + username))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Delete Account with valid Auth and non-existent Account")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void deleteAccount_withValidAuthAndInvalidUsername_returns404NotFound() throws Exception {
        String invalidUsername = account0.getUsername();
        doThrow(new ApiException("Account not found", HttpStatus.NOT_FOUND)).when(service).softDelete(invalidUsername);

        mockMvc.perform(delete("/api/v1/accounts/" + invalidUsername))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Delete existing Account with valid Auth")
    @WithMockUser(authorities = "STUDENT_ASSISTANT")
    void deleteAccount_wtihValidAuthAndValidUsername_returns200Ok() throws Exception {
        String validUsername = account0.getUsername();
        doNothing().when(service).softDelete(validUsername);

        mockMvc.perform(delete("/api/v1/accounts/" + validUsername))
                .andExpect(status().isOk());
    }

    private MockMultipartFile createDummyMultipartFile (List<Account> accountList, String contentType) {
        try {
            List<Account> accounts = new ArrayList<>(accountList);
            XSSFWorkbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Accounts");

            List<String> columns = List.of("ID", "Full name", "Username", "Email", "Type", "Created At", "Is Active", "Profile url", "Delete Flag");

            // Creating header row
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columns.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns.get(i));
            }

            // Populating the excel file
            for (int i = 0; i < accounts.size(); i++) {
                Row dataRow = sheet.createRow(i + 1);
                dataRow.createCell(0).setCellValue(accounts.get(i).getId());
                dataRow.createCell(1).setCellValue(accounts.get(i).getFullName());
                dataRow.createCell(2).setCellValue(accounts.get(i).getUsername());
                dataRow.createCell(3).setCellValue(accounts.get(i).getEmail());
                dataRow.createCell(4).setCellValue(accounts.get(i).getType().getName());
                dataRow.createCell(5).setCellValue(accounts.get(i).getCreatedAt().toString());
                dataRow.createCell(6).setCellValue(accounts.get(i).getActive());
                dataRow.createCell(7).setCellValue(accounts.get(i).getProfile());
                dataRow.createCell(8).setCellValue(accounts.get(i).getDeleteFlag());
            }

            // Making size of the columns auto resize to fit data
            for (int i = 0; i < columns.size(); i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return new MockMultipartFile("file", "Accounts.xlsx" , contentType,  new ByteArrayInputStream(outputStream.toByteArray()));
        } catch (IOException ignored) {
            return null;
        }
    }
}

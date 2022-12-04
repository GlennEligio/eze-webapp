package com.eze.backend.spring.service;

import static org.junit.jupiter.api.Assertions.*;

import com.eze.backend.spring.dtos.EzeUserDetails;
import com.eze.backend.spring.dtos.RegisterRequestDto;
import com.eze.backend.spring.enums.AccountType;
import com.eze.backend.spring.exception.ApiException;
import com.eze.backend.spring.model.Account;
import com.eze.backend.spring.repository.AccountRepository;
import com.eze.backend.spring.util.TimeStampProvider;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class AccountServiceTest {

    @Mock
    private AccountRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TimeStampProvider timeStampProvider;

    @InjectMocks
    private AccountService service;

    private List<Account> accountList = new ArrayList<>();
    private Account account0, account1;
    private LocalDateTime localDateTime;

    @BeforeEach
    void setup() {
        localDateTime = LocalDateTime.now();
        account0 = new Account(0L, "Name0", "Username0", "Email0", "Password0", AccountType.SA, "http://sampleurl.com/profile0", localDateTime, true, false);
        account1 = new Account(1L, "Name1", "Username1", "Email1", "Password1", AccountType.SA, "http://sampleurl.com/profile1", localDateTime, true, false);
        Account account2 = new Account(2L, "Name2", "Username2", "Email2", "Password2", AccountType.SA, "http://sampleurl.com/profile2", localDateTime, true, true);
        accountList = List.of(account1, account2, account0);
    }

    @Test
    @DisplayName("Get all Accounts")
    void getAll_returnsAccounts() {
        Mockito.when(repository.findAll()).thenReturn(accountList);

        List<Account> accounts = service.getAll();

        assertNotNull(accounts);
        assertEquals(accounts, accountList);
    }

    @Test
    @DisplayName("Get all not deleted Accounts")
    void getAllNotDeleted_returnsNotDeletedAccounts() {
        List<Account> notDeletedAccounts = accountList.stream().filter(a -> !a.getDeleteFlag()).toList();
        Mockito.when(repository.findAllNotDeleted()).thenReturn(notDeletedAccounts);

        List<Account> accounts = service.getAllNotDeleted();

        assertNotNull(accounts);
        assertEquals(notDeletedAccounts, accounts);
    }

    @Test
    @DisplayName("Get Account using valid Username")
    void get_usingValidUsername_returnsAccount() {
        String validUsername = "Username0";
        Mockito.when(repository.findByUsername(validUsername)).thenReturn(Optional.of(account0));

        Account account = service.get(validUsername);

        assertNotNull(account);
        assertEquals(account, account0);
    }

    @Test
    @DisplayName("Get Account using invalid Username")
    void get_usingInvalidUsername_throwsApiException() {
        String invalidUsername = "invalidUsername";
        Mockito.when(repository.findByUsername(invalidUsername)).thenThrow(new ApiException("Account with username passed was not found", HttpStatus.NOT_FOUND));

        assertThrows(ApiException.class, () -> service.get(invalidUsername));
    }

    // TODO: Check the problem: Shows different test result for each run
    @Test
    @DisplayName("Create Account using available username")
    void create_usingAvailableUsername_returnsNewAccount() {
        String encryptedPass = "EncryptedPassword";
        String oldPassword = account0.getPassword();
        Account newAccount = new Account(0L, "Name0", "Username0", "Email0", encryptedPass, AccountType.SA, "http://sampleurl.com/profile0", localDateTime, true, false);
        String availableUsername = newAccount.getUsername();
        Mockito.when(repository.findByUsername(availableUsername)).thenReturn(Optional.empty());
        Mockito.when(passwordEncoder.encode(oldPassword)).thenReturn(encryptedPass);
        Mockito.when(timeStampProvider.getNow()).thenReturn(localDateTime);
        Mockito.when(repository.save(newAccount)).thenReturn(newAccount);

        Account account = service.create(account0);

        assertNotNull(account);
        assertEquals(newAccount, account);
    }

    @Test
    @DisplayName("Create Account using already taken Username")
    void create_usingUnavailableUsername_throwsApiException() {
        String unavailableUsername = account0.getUsername();
        Mockito.when(repository.findByUsername(unavailableUsername)).thenReturn(Optional.of(account0));

        assertThrows(ApiException.class, () -> service.create(account0));
    }

    @Test
    @DisplayName("Create Account without Username included")
    void create_usingAccountWithNoUsername_throwsApiException() {
        account0.setUsername(null);

        assertThrows(ApiException.class, () -> service.create(account0));
    }

    // TODO: Check the problem: Shows different test result for each run
    @Test
    @DisplayName("Update existing Account")
    void update_usingExistingAccount_updatesAccount() {
        String encodedPassword = "EncodedPassword";
        Account accountForUpdate = new Account(null, "NewName0", "Username0", "Email0", "Password0", AccountType.SA, "http://sampleurl.com/profile0", localDateTime, true, false);
        Account updatedAccount  = new Account(account0.getId(), "NewName0", "Username0", "Email0", encodedPassword, AccountType.SA, "http://sampleurl.com/profile0", localDateTime, true, false);
        String validUsername = account0.getUsername();
        Mockito.when(repository.findByUsername(validUsername)).thenReturn(Optional.of(account0));
        Mockito.when(repository.save(updatedAccount)).thenReturn(updatedAccount);
        Mockito.when(passwordEncoder.encode(accountForUpdate.getPassword())).thenReturn(encodedPassword);

        Account accountResult = service.update(accountForUpdate, validUsername);

        assertNotNull(accountResult);
        assertEquals(updatedAccount, accountResult);
    }

    @Test
    @DisplayName("Update non-existent Account")
    void update_usingNonExistingAccounts_throwsApiException() {
        Account accountForUpdate = account1;
        String invalidUsername = account0.getUsername();
        Mockito.when(repository.findByUsername(invalidUsername)).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> service.update(accountForUpdate, invalidUsername));
    }

    @Test
    @DisplayName("Deletes existing Account")
    void delete_usingValidUsername_doesNotThrowException() {
        String validUsername = account0.getUsername();
        Mockito.when(repository.findByUsername(validUsername)).thenReturn(Optional.of(account0));

        assertDoesNotThrow(() -> service.delete(validUsername));
    }

    @Test
    @DisplayName("Deletes non-existing Account")
    void delete_usingInvalidUsername_throwException() {
        String invalidUsername = account0.getUsername();
        Mockito.when(repository.findByUsername(invalidUsername)).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> service.delete(invalidUsername));
    }

    @Test
    @DisplayName("Soft deletes a non-existent Account")
    void softDelete_usingInvalidUsername_throwsException() {
        String invalidUsername = account0.getUsername();
        Mockito.when(repository.findByUsername(invalidUsername)).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> service.softDelete(invalidUsername));
    }

    @Test
    @DisplayName("Soft deletes an existing Account that is soft deleted yet")
    void softDelete_usingValidUsernameOfDeletedAccount_throwsException() {
        String validUsername = account0.getUsername();
        account0.setDeleteFlag(true);
        Mockito.when(repository.findByUsername(validUsername)).thenReturn(Optional.of(account0));

        assertThrows(ApiException.class, () -> service.softDelete(validUsername));
    }

    @Test
    @DisplayName("Soft deletes an existing Account that is not soft deleted yet")
    void softDelete_usingValidUsernameOfNotDeletedAccount_doesNotException() {
        String validUsername = account0.getUsername();
        account0.setDeleteFlag(false);
        Mockito.when(repository.findByUsername(validUsername)).thenReturn(Optional.of(account0));

        assertDoesNotThrow(() -> service.softDelete(validUsername));
    }

    @Test
    @DisplayName("Creates a Not Found string")
    void notFound_createsCorrectString() {
        String username = account0.getUsername();
        String notFound = "No account with account username " + username + " was found";

        String notFoundResult = service.notFound(username);

        assertEquals(notFoundResult, notFound);
    }

    @Test
    @DisplayName("Creates an Already Existing string")
    void alreadyExist_createsAlreadyExistString() {
        String username = account0.getUsername();
        String alreadyExist = "Account with username " + username + " already exist";

        String alreadyExistResult = service.alreadyExist(username);

        assertEquals(alreadyExistResult, alreadyExist);
    }

    @Test
    @DisplayName("Add or Update Accounts using the same data and overwrite set to false")
    void addOrUpdate_usingListWithNoChangesAndNoOverwrite_returnsZero() {
        List<Account> accounts = List.of(account0);
        Mockito.when(repository.findByUsername(account0.getUsername())).thenReturn(Optional.of(account0));

        int itemsAffected = service.addOrUpdate(accounts, false);

        assertEquals(0, itemsAffected);
    }

    @Test
    @DisplayName("Add or Update Accounts using different data and overwrite set to false")
    void addOrUpdate_usingListWithChangesAndNoOverwrite_returnsZero() {
        account0.setActive(false);
        List<Account> accounts = List.of(account0);
        account0.setActive(true);
        Mockito.when(repository.findByUsername(account0.getUsername())).thenReturn(Optional.of(account0));

        int itemsAffected = service.addOrUpdate(accounts, false);

        assertEquals(0, itemsAffected);
    }

    @Test
    @DisplayName("Add or Update Accounts using different data and overwrite set to true")
    void addOrUpdate_usingListWithChangesAndOverwrite_returnsNonZero() {
        Account updatedAccount = new Account("NewName0", account0.getUsername(), "Email0", "Password0", AccountType.SA, "http://sampleurl.com/profile0", LocalDateTime.now(), true, false);
        List<Account> accounts = List.of(updatedAccount);
        Mockito.when(repository.findByUsername(account0.getUsername())).thenReturn(Optional.of(account0));

        int itemsAffected = service.addOrUpdate(accounts, true);

        assertNotEquals(0, itemsAffected);
    }

    @Test
    @DisplayName("Load User by Username using valid Username")
    void loadByUsername_usingValidUsername_returnsEzeUserDetails() {
        String validUsername = account0.getUsername();
        EzeUserDetails userDetails = new EzeUserDetails(account0);
        Mockito.when(repository.findByUsername(validUsername)).thenReturn(Optional.of(account0));

        EzeUserDetails userDetails1 = (EzeUserDetails) service.loadUserByUsername(validUsername);

        assertNotNull(userDetails1);
        assertEquals(userDetails, userDetails1);
    }

    @Test
    @DisplayName("Load User by Username using invalid Username")
    void loadByUsername_usingInvalidUsername_throwsException() {
        String invalidUsername = account0.getUsername();
        Mockito.when(repository.findByUsername(invalidUsername)).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> service.get(invalidUsername));
    }

    @Test
    @DisplayName("Create Excel from a List of Account")
    void listToExcel_returnsExcelWithSameData() {
        try {
            List<Account> accounts = new ArrayList<>(accountList);
            List<String> columns = List.of("ID", "Full name", "Username", "Email", "Type", "Created At", "Is Active", "Profile url", "Delete Flag");

            ByteArrayInputStream inputStream = service.listToExcel(accounts);
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheetAt(0);

            Row headerRow = sheet.getRow(0);
            for (int i = 0; i < columns.size(); i++) {
                String columnName = headerRow.getCell(i).getStringCellValue();
                assertEquals(columns.get(i), columnName);
            }

            for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                Row dataRow = sheet.getRow(i);
                Account account = accounts.get(i - 1);
                assertEquals(account.getId(), (long) dataRow.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getNumericCellValue());
                assertEquals(account.getFullName(), dataRow.getCell(1).getStringCellValue());
                assertEquals(account.getUsername(), dataRow.getCell(2).getStringCellValue());
                assertEquals(account.getEmail(), dataRow.getCell(3).getStringCellValue());
                assertEquals(account.getType(), AccountType.of(dataRow.getCell(4).getStringCellValue()));
                assertEquals(account.getCreatedAt(), LocalDateTime.parse(dataRow.getCell(5).getStringCellValue()));
                assertEquals(account.getActive(), dataRow.getCell(6).getBooleanCellValue());
                assertEquals(account.getProfile(), dataRow.getCell(7).getStringCellValue());
                assertEquals(account.getDeleteFlag(), dataRow.getCell(8).getBooleanCellValue());
            }
        } catch (IOException ignored) {

        }
    }

    @Test
    @DisplayName("Create List of Account from Multipart file")
    void excelToList_returnsListOfAccount() {
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
            MultipartFile file = new MockMultipartFile("file", new ByteArrayInputStream(outputStream.toByteArray()));

            List<Account> accountsResult = service.excelToList(file);

            assertNotEquals(0, accountsResult.size());
            for (int i = 0; i < accountList.size(); i++) {
                Account accountExpected = accounts.get(i);
                Account accountResult = accountsResult.get(i);
                accountExpected.setPassword(null);
                assertEquals(accountExpected, accountResult);
            }
        } catch (IOException ignored) {

        }
    }
}

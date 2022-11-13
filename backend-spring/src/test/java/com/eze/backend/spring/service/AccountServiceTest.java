package com.eze.backend.spring.service;

import static org.junit.jupiter.api.Assertions.*;

import com.eze.backend.spring.dtos.RegisterRequestDto;
import com.eze.backend.spring.enums.AccountType;
import com.eze.backend.spring.exception.ApiException;
import com.eze.backend.spring.model.Account;
import com.eze.backend.spring.repository.AccountRepository;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ITimeStampProvider timeStampProvider;

    @InjectMocks
    private AccountService service;

    private List<Account> accountList = new ArrayList<>();
    private Account account0, account1;
    private  LocalDateTime localDateTime;

    @BeforeEach
    void setup() {
        account0 = new Account("Name0", "Username0", "Email0", "Password0", AccountType.SA, "http://sampleurl.com/profile0", LocalDateTime.now(), true, false);
        account1 = new Account("Name1", "Username1", "Email1", "Password1", AccountType.SA, "http://sampleurl.com/profile1", LocalDateTime.now(), true, false);
        Account account2 = new Account("Name2", "Username2", "Email2", "Password2", AccountType.SA, "http://sampleurl.com/profile2", LocalDateTime.now(), true, true);
        accountList = List.of(account1, account2, account0);
        localDateTime = LocalDateTime.now();
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

    @Test
    @DisplayName("Create Account using available username")
    void create_usingAvailableUsername_returnsNewAccount() {
        String availableUsername = account0.getUsername();
        String encryptedPass = "EncryptedPassword";
        Account newAccount = new Account(account0.getFullName(),
                account0.getUsername(),
                account0.getEmail(),
                encryptedPass,
                account0.getType(),
                account0.getProfile(),
                localDateTime,
                account0.getActive(),
                account0.getDeleteFlag());
        Mockito.when(repository.findByUsername(availableUsername)).thenReturn(Optional.empty());
        Mockito.when(passwordEncoder.encode(account0.getPassword())).thenReturn(encryptedPass);
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

    @Test
    @DisplayName("Update existing Account")
    void update_usingExistingAccount_updatesAccount() {
        Account accountForUpdate = account1;
        String validUsername = account0.getUsername();
        Mockito.when(repository.findByUsername(validUsername)).thenReturn(Optional.of(account0));
        Mockito.when(repository.save(accountForUpdate)).thenReturn(accountForUpdate);

        Account updatedAccount = service.update(accountForUpdate, validUsername);

        assertNotNull(updatedAccount);
        assertEquals(updatedAccount, accountForUpdate);
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

        assertThrows(ApiException.class,() -> service.delete(invalidUsername));
    }
}

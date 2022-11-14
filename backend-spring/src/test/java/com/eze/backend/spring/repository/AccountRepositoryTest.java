package com.eze.backend.spring.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.eze.backend.spring.enums.AccountType;
import com.eze.backend.spring.model.Account;
import com.eze.backend.spring.repository.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import javax.swing.text.html.Option;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@DataJpaTest
@Slf4j
class AccountRepositoryTest {

    @Autowired
    private AccountRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private Account account0;

    @BeforeEach
    void setup() {
        // Define Entity and persist it
        account0 = new Account("Name0", "Username0", "Email0", "Password0", AccountType.SA, "http://sampleurl.com/profile0", LocalDateTime.now(), true, false);
        Account account1 = new Account("Name1", "Username1", "Email1", "Password1", AccountType.SA, "http://sampleurl.com/profile1", LocalDateTime.now(), true, false);
        Account account2 = new Account("Name2", "Username2", "Email2", "Password2", AccountType.SA, "http://sampleurl.com/profile2", LocalDateTime.now(), true, true);
        entityManager.persist(account0);
        entityManager.persist(account1);
        entityManager.persist(account2);
    }

    @Test
    @DisplayName("Find Existing Account by Username")
    void findByUsername_usingValidUsername_returnAccount() {
        String validUsername = "Username0";

        Optional<Account> accountOptional = repository.findByUsername(validUsername);

        assertTrue(accountOptional.isPresent());
        assertEquals(accountOptional.get(), account0);
    }

    @Test
    @DisplayName("Find Account with invalid Username")
    void findByUsername_usingInvalidUsername_returnsEmpty() {
        String invalidUsername = "invalidUsername";

        Optional<Account> accountOptional = repository.findByUsername(invalidUsername);

        assertTrue(accountOptional.isEmpty());
    }

    @Test
    @DisplayName("Find All Non-deleted Accounts")
    void findAllNotDeleted_returnsNonDeletedAccounts() {
        List<Account> accountList = repository.findAllNotDeleted();

        assertEquals(0, accountList.stream().filter(Account::getDeleteFlag).count());
    }

    @Test
    @DisplayName("Update deleteFlag of an Account")
    void softDelete_updatesAccountDeleteFlag() {
        String validUsername = "Username0";

        repository.softDelete(validUsername);
        Optional<Account> updatedAccount = repository.findByUsername(validUsername);

        assertTrue(updatedAccount.isPresent());
        assertTrue(updatedAccount.get().getDeleteFlag());
    }
}

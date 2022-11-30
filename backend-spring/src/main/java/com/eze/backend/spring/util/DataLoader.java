package com.eze.backend.spring.util;

import com.eze.backend.spring.enums.AccountType;
import com.eze.backend.spring.model.Account;
import com.eze.backend.spring.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@Slf4j
public class DataLoader implements ApplicationRunner {

    // Fetch JWT in Config props
    @Value("${eze.sadmin-username}")
    private String SADMIN_USERNAME;

    @Value("${eze.sadmin-password}")
    private String SADMIN_PASSWORD;

    @Value("${eze.sadmin-email}")
    private String SADMIN_EMAIL;

    @Autowired
    private AccountService accountService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<Account> accounts = accountService.getAll();
        if(accounts.stream().anyMatch(a -> Objects.equals(a.getUsername(), "sadmin"))) return;

        Account account1 = new Account();
        account1.setUsername(SADMIN_USERNAME);
        account1.setPassword(SADMIN_PASSWORD);
        account1.setActive(true);
        account1.setType(AccountType.SADMIN);
        account1.setEmail(SADMIN_EMAIL);
        account1.setFullName("SADMIN");
        log.info("Adding an SADMIN type account");
        accountService.create(account1);
    }
}

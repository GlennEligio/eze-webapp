package com.eze.backend.restapi.util;

import com.eze.backend.restapi.enums.AccountType;
import com.eze.backend.restapi.model.Account;
import com.eze.backend.restapi.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class DataLoader implements ApplicationRunner {

    @Autowired
    private AccountService accountService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<Account> accounts = accountService.getAll();
        if(accounts.stream().anyMatch(a -> Objects.equals(a.getUsername(), "sadmin"))) return;

        Account account1 = new Account();
        // TODO: Fetch the sadmin credentials from environment variables -> application.properties using @Value
        account1.setUsername("sadmin");
        account1.setPassword("password");
        account1.setActive(true);
        account1.setType(AccountType.SADMIN);
        accountService.create(account1);
    }
}
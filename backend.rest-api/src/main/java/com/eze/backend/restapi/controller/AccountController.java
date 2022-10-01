package com.eze.backend.restapi.controller;

import com.eze.backend.restapi.model.Account;
import com.eze.backend.restapi.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class AccountController {

    @Autowired
    private AccountService service;

    @GetMapping("/accounts")
    public ResponseEntity<List<Account>> getAccounts() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/accounts/{code}")
    public ResponseEntity<Account> getAccount(@PathVariable("code") String code) {
        return ResponseEntity.ok(service.get(code));
    }

    @PostMapping("/accounts")
    public ResponseEntity<Account> createAccount(@RequestBody Account account) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(account));
    }

    @PutMapping("/accounts/{code}")
    public ResponseEntity<Account> updateAccount(@RequestBody Account account,
                                                 @PathVariable("code") String code) {
        return ResponseEntity.ok(service.update(account, code));
    }

    @DeleteMapping("/accounts/{code}")
    public ResponseEntity<Object> deleteAccount(@PathVariable("code") String code) {
        service.delete(code);
        return ResponseEntity.ok().build();
    }
}

package com.eze.backend.restapi.controller;

import com.eze.backend.restapi.model.Transaction;
import com.eze.backend.restapi.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class TransactionController {

    @Autowired
    private TransactionService service;

    @GetMapping("/transactions")
    public ResponseEntity<List<Transaction>> getTransactions() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/transactions/{code}")
    public ResponseEntity<Transaction> getTransaction(@PathVariable("code") String code) {
        return ResponseEntity.ok(service.get(code));
    }

    @PostMapping("/transactions")
    public ResponseEntity<Transaction> createTransaction(@RequestBody Transaction transaction) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(transaction));
    }

    @PutMapping("/transactions/{code}")
    public ResponseEntity<Transaction> updateTransaction(@RequestBody Transaction transaction,
                                                         @PathVariable("code") String code) {
        return ResponseEntity.ok(service.update(transaction, code));
    }

    @DeleteMapping("/transactions/{code}")
    public ResponseEntity<Object> deleteTransaction(@PathVariable("code") String code) {
        service.delete(code);
        return ResponseEntity.ok().build();
    }
}

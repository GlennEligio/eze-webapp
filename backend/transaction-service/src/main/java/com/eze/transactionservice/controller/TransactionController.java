package com.eze.transactionservice.controller;

import com.eze.transactionservice.domain.Transaction;
import com.eze.transactionservice.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class TransactionController {

    private final TransactionService service;

    public TransactionController (TransactionService service) {
        this.service = service;
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<Transaction>> getAllTransactions(){
        return ResponseEntity.ok(service.findAllTransactions());
    }

    @GetMapping("/transactions/{transactionId}")
    public ResponseEntity<Transaction> getTransaction(@PathVariable("transactionId") String transactionId){
        return ResponseEntity.ok(service.findTransaction(transactionId));
    }

    @PostMapping("/transactions")
    public ResponseEntity<Transaction> createTransaction(@Valid @RequestBody Transaction transaction){
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createTransaction(transaction));
    }

    @PutMapping("/transactions")
    public ResponseEntity<Object> updateTransaction(@Valid @RequestBody Transaction transaction) {
        service.updateTransaction(transaction);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/transactions/{transactionId}")
    public ResponseEntity<Object> deleteTransaction(@PathVariable("transactionId") String transactionId) {
        service.deleteTransaction(transactionId);
        return ResponseEntity.ok().build();
    }
}

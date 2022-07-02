package com.eze.transactionservice.controller;

import com.eze.transactionservice.domain.Status;
import com.eze.transactionservice.domain.Transaction;
import com.eze.transactionservice.dtos.TransactionDto;
import com.eze.transactionservice.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Slf4j
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
    public ResponseEntity<Transaction> createTransaction(@RequestBody TransactionDto transaction){
        Transaction newTransaction = Transaction.builder()
                .transactionItems(transaction.getTransactionItems())
                .requestedBy(transaction.getRequestedBy())
                .acceptedBy(transaction.getAcceptedBy())
                .status(transaction.getStatus())
                .deleteFlag(transaction.getDeleteFlag())
                .dateCreated(transaction.getDateCreated())
                .dateResolved(transaction.getDateResolved())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createTransaction(newTransaction));
    }

    @PutMapping("/transactions")
    public ResponseEntity<Object> updateTransaction(@RequestBody TransactionDto transaction) {
        Transaction updatedTransaction = Transaction.builder()
                .transactionCode(transaction.getTransactionCode())
                .requestedBy(transaction.getRequestedBy())
                .acceptedBy(transaction.getAcceptedBy())
                .status(transaction.getStatus())
                .dateCreated(transaction.getDateCreated())
                .dateResolved(transaction.getDateResolved())
                .transactionItems(transaction.getTransactionItems())
                .deleteFlag(transaction.getDeleteFlag())
                .build();
        service.updateTransaction(updatedTransaction);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/transactions/{transactionId}")
    public ResponseEntity<Object> deleteTransaction(@PathVariable("transactionId") String transactionId) {
        service.deleteTransaction(transactionId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/transactions/{transactionId}/{status}")
    public ResponseEntity<Object> updateTransactionStatus(@PathVariable("transactionId") String transactionId,
                                                          @PathVariable("status") String status){
        Transaction transaction = service.findTransaction(transactionId);
        transaction.setStatus(Status.of(status));
        transaction.setDateResolved(LocalDateTime.now());
        service.updateTransaction(transaction);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/transactions/professor/{profId}/{status}")
    public ResponseEntity<List<Transaction>> getTransactionProfessor(@PathVariable("profId") String profId,
                                                                     @PathVariable("status") String status){
        return ResponseEntity.ok(service.findProfessorTransactions(profId, status));
    }

    @GetMapping("/transactions/student/{studentId}/{status}")
    public ResponseEntity<List<Transaction>> getTransactionStudent(@PathVariable("studentId") String studentId,
                                                                     @PathVariable("status") String status){
        return ResponseEntity.ok(service.findStudentTransactions(studentId, status));
    }
}

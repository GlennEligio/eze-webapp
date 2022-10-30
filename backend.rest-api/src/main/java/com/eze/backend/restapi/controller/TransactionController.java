package com.eze.backend.restapi.controller;

import com.eze.backend.restapi.dtos.TransactionDto;
import com.eze.backend.restapi.dtos.TransactionListDto;
import com.eze.backend.restapi.model.Transaction;
import com.eze.backend.restapi.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Slf4j
public class TransactionController {

    @Autowired
    private TransactionService service;

    @GetMapping("/transactions")
    public ResponseEntity<List<?>> getTransactions(@RequestParam(required = false) String complete) {
        if(complete != null && complete.equalsIgnoreCase("true")) {
            return ResponseEntity.ok(service.getAll().stream().map(Transaction::toTransactionDto).toList());
        }
        return ResponseEntity.ok(service.getAll().stream().map(Transaction::toTransactionListDto).toList());
    }

    @GetMapping("/transactions/{code}")
    public ResponseEntity<Object> getTransaction(@PathVariable("code") String code,
                                                 @RequestParam(required = false) String complete) {
        if (complete != null && complete.equalsIgnoreCase("true")) {
            return ResponseEntity.ok(Transaction.toTransactionDto(service.get(code)));
        }
        return ResponseEntity.ok(Transaction.toTransactionListDto(service.get(code)));
    }

    // TODO: Add option to receive either TX with full equipments list or only eq count version of new Transaction
    @PostMapping("/transactions")
    public ResponseEntity<Object> createTransaction(@RequestBody Transaction transaction,
                                                    @RequestParam(required = false) String complete) {
        if (complete != null && complete.equalsIgnoreCase("true")) {
            return ResponseEntity.status(HttpStatus.CREATED).body(Transaction.toTransactionDto(service.create(transaction)));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(Transaction.toTransactionListDto(service.create(transaction)));
    }

    @PutMapping("/transactions")
    public ResponseEntity<TransactionDto> updateTransaction(@RequestBody Transaction transaction,
                                                            @RequestParam String code) {
        return ResponseEntity.ok(Transaction.toTransactionDto(service.update(transaction, code)));
    }

    @DeleteMapping("/transactions/{code}")
    public ResponseEntity<Object> deleteTransaction(@PathVariable("code") String code) {
        service.delete(code);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/transactions/return")
    public ResponseEntity<TransactionListDto> returnEquipments(@RequestParam String borrower,
                                                               @RequestParam String professor,
                                                               @RequestParam String[] barcodes) {
        return ResponseEntity.ok(Transaction.toTransactionListDto(service.returnEquipments(borrower, professor, Arrays.stream(barcodes).toList())));
    }
}

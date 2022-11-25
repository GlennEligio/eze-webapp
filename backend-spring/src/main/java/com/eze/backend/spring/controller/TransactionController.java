package com.eze.backend.spring.controller;

import com.eze.backend.spring.dtos.*;
import com.eze.backend.spring.enums.TxStatus;
import com.eze.backend.spring.exception.ApiException;
import com.eze.backend.spring.model.Transaction;
import com.eze.backend.spring.service.TransactionService;
import com.eze.backend.spring.validation.EnumNamePattern;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/v1")
@Slf4j
public class TransactionController {

    @Autowired
    private TransactionService service;

    @GetMapping("/transactions")
    public ResponseEntity<List<?>> getTransactions(@RequestParam(required = false, defaultValue = "false") Boolean complete,
                                                   @RequestParam(required = false, defaultValue = "false") Boolean historical,
                                                   @RequestParam(required = false) Boolean returned,
                                                   @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate,
                                                   @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate) {
        Stream<Transaction> transactions = service.getAll().stream();

        // if returned is false, only get transactions with no (duplicable) equipments in the "equipments" property
        if (returned != null) {
            if (Boolean.TRUE.equals(returned)) {
                transactions = transactions.filter(t -> t.getEquipments()
                        .stream()
                        .filter(equipment -> !equipment.getIsDuplicable())
                        .toList().isEmpty());
            } else {
                transactions = transactions.filter(t -> !t.getEquipments()
                        .stream()
                        .filter(equipment -> !equipment.getIsDuplicable())
                        .toList().isEmpty());
            }
        }

        // if fromDate and toDate is present, filter the transactions again
        if (fromDate != null && toDate != null) {
            transactions = transactions.filter(t -> t.getBorrowedAt().isAfter(fromDate) && t.getBorrowedAt().isBefore(toDate));
        }

        // historical will determine if we will use the equipment (current unreturned eqs) or equipmentHist (saved equipments history)
        // complete will determine if we will send the equipments with complete info or just send the number of equipments in transaction (unreturned or historical)
        if (Boolean.TRUE.equals(historical)) {
            if (Boolean.TRUE.equals(complete)) {
                return ResponseEntity.ok(transactions.map(Transaction::toTransactionHistDto).toList());
            } else {
                return ResponseEntity.ok(transactions.map(Transaction::toTransactionHistListDto).toList());
            }
        } else {
            if (Boolean.TRUE.equals(complete)) {
                return ResponseEntity.ok(transactions.map(Transaction::toTransactionDto).toList());
            } else {
                return ResponseEntity.ok(transactions.map(Transaction::toTransactionListDto).toList());
            }
        }
    }

    @GetMapping("/transactions/student/{studentNumber}")
    public ResponseEntity<List<?>> getStudentTransactions(@PathVariable String studentNumber,
                                                          @RequestParam(defaultValue = "false") boolean returned,
                                                          @RequestParam(defaultValue = "false") boolean historical,
                                                          @RequestParam(required = false) String status) {
        Stream<Transaction> studentTransactions = service.getStudentTransactions(studentNumber).stream();
        if (status != null) {
            studentTransactions = studentTransactions.filter(t -> t.getStatus().getName().equalsIgnoreCase(status));
        }

        if (returned) {
            studentTransactions = studentTransactions.filter(t -> t.getEquipments().isEmpty());
        } else {
            studentTransactions = studentTransactions.filter(t -> !t.getEquipments().isEmpty());
        }

        if (historical) {
            List<TransactionHistListDto> transactionHistListDto = studentTransactions.map(Transaction::toTransactionHistListDto).toList();
            return ResponseEntity.ok(transactionHistListDto);
        }
        List<TransactionListDto> transactionListDtos = studentTransactions.map(Transaction::toTransactionListDto).toList();
        return ResponseEntity.ok(transactionListDtos);
    }

    @GetMapping("/transactions/professor/{name}")
    public ResponseEntity<List<?>> getProfessorTransactions(@PathVariable String name,
                                                            @RequestParam(defaultValue = "false") boolean returned,
                                                            @RequestParam(defaultValue = "false") boolean historical) {
        Stream<Transaction> professorTransactions = service.getProfessorTransactions(name).stream();
        if (returned) {
            professorTransactions = professorTransactions.filter(t -> t.getEquipments().isEmpty());
        } else {
            professorTransactions = professorTransactions.filter(t -> !t.getEquipments().isEmpty());
        }

        if (historical) {
            List<TransactionHistListDto> transactionHistListDto = professorTransactions.map(Transaction::toTransactionHistListDto).toList();
            return ResponseEntity.ok(transactionHistListDto);
        }
        List<TransactionListDto> transactionListDtos = professorTransactions.map(Transaction::toTransactionListDto).toList();
        return ResponseEntity.ok(transactionListDtos);
    }

    @GetMapping("/transactions/download")
    public void download(HttpServletResponse response,
                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate,
                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate) throws IOException {

        log.info("Preparing Transactions list for Download");
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=transactions.xlsx");
        Stream<Transaction> transactions = service.getAll().stream();

        // if fromDate and toDate is present, filter the transactions again
        if (fromDate != null && toDate != null) {
            transactions = transactions.filter(t -> t.getBorrowedAt().isAfter(fromDate) && t.getBorrowedAt().isBefore(toDate));
            response.setHeader("Content-Disposition", "attachment; filename=transactions(" + fromDate + "-" + toDate + ").xlsx");
        }

        ByteArrayInputStream stream = service.listToExcel(transactions.toList());
        IOUtils.copy(stream, response.getOutputStream());
    }

    @PostMapping("/transactions/upload")
    public ResponseEntity<Object> upload(@RequestParam(required = false, defaultValue = "false") Boolean overwrite,
                                         @RequestParam MultipartFile file) {
        log.info("Preparing Excel for Transaction Database update");
        if (!Objects.equals(file.getContentType(), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
            throw new ApiException("Can only upload .xlsx files", HttpStatus.BAD_REQUEST);
        }
        List<Transaction> transactions = service.excelToList(file);
        log.info("Got the transactions from excel");
        int itemsAffected = service.addOrUpdate(transactions, overwrite);
        log.info("Successfully updated {} transactions database using the excel file", itemsAffected);
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("Transactions Affected", itemsAffected);
        return ResponseEntity.ok(objectNode);
    }

    @GetMapping("/transactions/{code}")
    public ResponseEntity<Object> getTransaction(@PathVariable("code") String code,
                                                 @RequestParam(required = false, defaultValue = "false") boolean complete,
                                                 @RequestParam(required = false, defaultValue = "false") boolean historical) {
        Transaction transaction = service.get(code);
        // condition statements for complete details and historical data
        if (Boolean.TRUE.equals(complete)) {
            if (Boolean.TRUE.equals(historical)) {
                TransactionHistDto dto = Transaction.toTransactionHistDto(transaction);
                return ResponseEntity.ok(dto);
            } else {
                TransactionDto dto = Transaction.toTransactionDto(transaction);
                return ResponseEntity.ok(dto);
            }
        } else {
            if (Boolean.TRUE.equals(historical)) {
                TransactionHistListDto dto = Transaction.toTransactionHistListDto(transaction);
                return ResponseEntity.ok(dto);
            } else {
                TransactionListDto dto = Transaction.toTransactionListDto(transaction);
                return ResponseEntity.ok(dto);
            }
        }
    }

    @PostMapping("/transactions")
    public ResponseEntity<Object> createTransaction(@Valid @RequestBody CreateUpdateTransactionDto dto,
                                                    @RequestParam(required = false, defaultValue = "false") Boolean complete) {
        Transaction transactionToCreate = Transaction.toTransaction(dto);
        Transaction createdTransaction = service.create(transactionToCreate);
        if (Boolean.TRUE.equals(complete)) {
            TransactionDto transactionDto = Transaction.toTransactionDto(createdTransaction);
            return ResponseEntity.status(HttpStatus.CREATED).body(transactionDto);
        }
        TransactionListDto transactionListDto = Transaction.toTransactionListDto(createdTransaction);
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionListDto);
    }

    @PutMapping("/transactions")
    public ResponseEntity<TransactionDto> updateTransaction(@Valid @RequestBody CreateUpdateTransactionDto dto,
                                                            @RequestParam String code) {
        Transaction transactionForUpdate = Transaction.toTransaction(dto);
        Transaction updatedTransaction = service.update(transactionForUpdate, code);
        TransactionDto dtoResponse = Transaction.toTransactionDto(updatedTransaction);
        return ResponseEntity.ok(dtoResponse);
    }

    @DeleteMapping("/transactions/{code}")
    public ResponseEntity<Object> deleteTransaction(@PathVariable("code") String code) {
        service.softDelete(code);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/transactions/return")
    public ResponseEntity<TransactionListDto> returnEquipments(@RequestParam String borrower,
                                                               @RequestParam String professor,
                                                               @NotEmpty(message = "Barcodes can't be empty") @RequestParam String[] barcodes) {
        Transaction transaction = service.returnEquipments(borrower, professor, Arrays.stream(barcodes).toList());
        TransactionListDto transactionListDto = Transaction.toTransactionListDto(transaction);
        return ResponseEntity.ok(transactionListDto);
    }
}

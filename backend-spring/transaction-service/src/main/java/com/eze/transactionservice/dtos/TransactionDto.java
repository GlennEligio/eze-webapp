package com.eze.transactionservice.dtos;

import com.eze.transactionservice.domain.Status;
import com.eze.transactionservice.domain.TransactionItem;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class TransactionDto {
    private Long id;
    private String transactionCode;
    private List<TransactionItem> transactionItems;
    private String requestedBy;
    private String acceptedBy;
    private Status status;
    private LocalDateTime dateCreated;
    private LocalDateTime dateResolved;
    private Boolean deleteFlag;
}

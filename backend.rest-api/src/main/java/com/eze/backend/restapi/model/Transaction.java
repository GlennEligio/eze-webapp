package com.eze.backend.restapi.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {
    private Long id;
    private List<Equipment> equipments;
    private Student borrower;
    private Professor professor;
    private LocalDateTime borrowedAt;
    private LocalDateTime returnedAt;
    // will be Status enum later
    private String status;
}

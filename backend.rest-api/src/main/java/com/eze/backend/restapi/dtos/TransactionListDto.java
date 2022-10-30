package com.eze.backend.restapi.dtos;

import com.eze.backend.restapi.enums.TxStatus;

import java.time.LocalDateTime;

public record TransactionListDto (String txCode,
                              Integer equipmentsCount,
                              String borrower,
                              String yearAndSection,
                              String professor,
                              String borrowedAt,
                              String returnedAt,
                              TxStatus status) { }

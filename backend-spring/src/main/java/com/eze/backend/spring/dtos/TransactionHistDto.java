package com.eze.backend.spring.dtos;

import com.eze.backend.spring.enums.TxStatus;

import java.time.LocalDateTime;
import java.util.List;

public record TransactionHistDto (String txCode,
                              List<EquipmentDto> equipmentsHistory,
                              String borrower,
                              String yearAndSection,
                              String professor,
                              LocalDateTime borrowedAt,
                              LocalDateTime returnedAt,
                              TxStatus status) { }

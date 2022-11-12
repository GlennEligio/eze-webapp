package com.eze.backend.spring.dtos;

import com.eze.backend.spring.enums.TxStatus;

public record TransactionHistListDto (String txCode,
                                  Integer equipmentsHistCount,
                                  String borrower,
                                  String yearAndSection,
                                  String professor,
                                  String borrowedAt,
                                  String returnedAt,
                                  TxStatus status) { }

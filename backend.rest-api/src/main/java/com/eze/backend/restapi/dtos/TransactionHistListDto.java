package com.eze.backend.restapi.dtos;

import com.eze.backend.restapi.enums.TxStatus;

public record TransactionHistListDto (String txCode,
                                  Integer equipmentsHistCount,
                                  String borrower,
                                  String yearAndSection,
                                  String professor,
                                  String borrowedAt,
                                  String returnedAt,
                                  TxStatus status) { }

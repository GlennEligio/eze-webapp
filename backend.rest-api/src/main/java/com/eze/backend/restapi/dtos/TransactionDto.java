package com.eze.backend.restapi.dtos;

import com.eze.backend.restapi.enums.TxStatus;

import java.time.LocalDateTime;
import java.util.List;

public record TransactionDto (String txCode,
                                  List<EquipmentDto> equipments,
                                  String borrower,
                                  String yearAndSection,
                                  String professor,
                                  LocalDateTime borrowedAt,
                                  LocalDateTime returnedAt,
                                  TxStatus status) { }
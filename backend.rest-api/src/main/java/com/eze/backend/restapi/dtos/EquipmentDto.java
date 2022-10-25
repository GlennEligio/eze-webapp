package com.eze.backend.restapi.dtos;

import com.eze.backend.restapi.enums.EqStatus;

import java.time.LocalDateTime;

public record EquipmentDto (Long id,
                            String equipmentCode,
                            String name,
                            String barcode,
                            EqStatus status,
                            LocalDateTime defectiveSince,
                            Boolean isDuplicable,
                            Boolean isBorrowed) { }

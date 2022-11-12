package com.eze.backend.spring.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class EquipmentDto {
    private Long id;
    private String equipmentCode;
    @NotBlank(message = "Equipment's name can't be blank")
    private String name;
    @NotBlank(message = "Equipment's barcode can't be blank")
    private String barcode;
    @NotNull(message = "Status must be present")
    @Pattern(regexp = "^(DEFECTIVE|GOOD)", message = "Equipment's status can only either be 'DEFECTIVE' or 'GOOD'")
    private String status;
    private LocalDateTime defectiveSince;
    @NotNull(message = "Equipment must be defined to be either duplicable or not")
    private Boolean isDuplicable;
    private Boolean isBorrowed;
}

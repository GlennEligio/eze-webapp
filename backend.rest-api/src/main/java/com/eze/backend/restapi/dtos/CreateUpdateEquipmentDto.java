package com.eze.backend.restapi.dtos;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class CreateUpdateEquipmentDto {
    @NotBlank(message = "Equipment's name can't be blank")
    private String name;
    @NotBlank(message = "Equipment's barcode can't be blank")
    private String barcode;
    @Pattern(regexp = "^(DEFECTIVE|GOOD)", message = "Equipment's status can only either be 'DEFECTIVE' or 'GOOD'")
    private String status;
    private String defectiveSince;
    @NotNull(message = "Equipment must be defined to be either duplicable or not")
    private Boolean isDuplicable;
}

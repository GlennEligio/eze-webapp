package com.eze.backend.restapi.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUpdateTransactionDto {
    @Valid
    @NotEmpty(message = "Equipments cant be empty")
    @NotNull(message = "Equipments must be present")
    private List<EquipmentDto> equipments;
    @NotNull(message = "Borrower must be present")
    @Valid
    private StudentDto borrower;
    @NotNull(message = "Professor must be present")
    @Valid
    private ProfessorDto professor;
    @NotBlank(message = "Status can't be blank")
    @Pattern(regexp = "^(PENDING|ACCEPTED|DENIED)", message = "Transaction's status can only either be 'PENDING', 'ACCEPTED', or 'DENIED'")
    private String status;
}

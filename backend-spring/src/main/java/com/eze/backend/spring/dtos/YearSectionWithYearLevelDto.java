package com.eze.backend.spring.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class YearSectionWithYearLevelDto {
    private Long id;
    @NotBlank(message = "Section name can't be blank")
    private String sectionName;
    @NotNull(message = "Year level must be present")
    @Valid
    private YearLevelDto yearLevel;
}

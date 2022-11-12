package com.eze.backend.spring.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class YearSectionDto {
    private Long id;
    @NotBlank(message = "Section name can't be blank")
    private String sectionName;
}

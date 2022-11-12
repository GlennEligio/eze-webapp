package com.eze.backend.spring.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class YearLevelDto {
    private Long id;
    @Positive(message = "Year number must be greater than 0")
    @NotNull(message = "Year number must be present")
    private Integer yearNumber;
    private String yearName;
}

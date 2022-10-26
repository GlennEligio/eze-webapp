package com.eze.backend.restapi.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class YearLevelDto {
    private Long id;
    private Integer yearNumber;
    private String yearName;
}

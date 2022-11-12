package com.eze.backend.spring.dtos;

import java.util.List;

public record YearLevelWithSectionsDto (Long id, Integer yearNumber, String yearName, List<YearSectionDto> yearSections){
}

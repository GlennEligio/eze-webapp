package com.eze.backend.restapi.dtos;

import java.util.List;

public record YearLevelDto (Long id, Integer yearNumber, String yearName, List<YearSectionDto> yearSections){}

package com.eze.backend.restapi.controller;

import com.eze.backend.restapi.dtos.YearSectionDto;
import com.eze.backend.restapi.dtos.YearSectionWithYearLevelDto;
import com.eze.backend.restapi.model.YearSection;
import com.eze.backend.restapi.service.YearSectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class YearSectionController {

    @Autowired
    private YearSectionService service;

    @GetMapping("/yearSections")
    public ResponseEntity<List<YearSectionDto>> getYearSections() {
        return ResponseEntity.ok(service.getAll().stream().map(YearSection::toYearSectionDto).toList());
    }

    @GetMapping("/yearSections/{sectionName}")
    public ResponseEntity<YearSectionWithYearLevelDto> getYearSection(@PathVariable("sectionName") String sectionName) {
        return ResponseEntity.ok(YearSection.toYearSectionWithYearLevelDto(service.get(sectionName)));
    }

    @PostMapping("/yearSections")
    public ResponseEntity<YearSectionWithYearLevelDto> createYearSection(@Valid @RequestBody YearSectionWithYearLevelDto dto) {
        return ResponseEntity.ok(YearSection.toYearSectionWithYearLevelDto(service.create(YearSection.toYearSection(dto))));
    }

    @PutMapping("/yearSections/{sectionName}")
    public ResponseEntity<YearSectionWithYearLevelDto> updateYearSection(@Valid @RequestBody YearSectionWithYearLevelDto dto,
                                                         @PathVariable("sectionName") String sectionName) {
        return ResponseEntity.ok(YearSection.toYearSectionWithYearLevelDto(service.update(YearSection.toYearSection(dto), sectionName)));
    }

    @DeleteMapping("/yearSections/{sectionName}")
    public ResponseEntity<YearSection> deleteYearSection(@PathVariable("sectionName") String sectionName) {
        service.delete(sectionName);
        return ResponseEntity.ok().build();
    }
}

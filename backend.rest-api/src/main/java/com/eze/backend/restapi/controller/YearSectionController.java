package com.eze.backend.restapi.controller;

import com.eze.backend.restapi.model.YearSection;
import com.eze.backend.restapi.service.YearSectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class YearSectionController {

    @Autowired
    private YearSectionService service;

    @GetMapping("/yearSections")
    public ResponseEntity<List<YearSection>> getYearSections() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/yearSections/{sectionName}")
    public ResponseEntity<YearSection> getYearSection(@PathVariable("sectionName") String sectionName) {
        return ResponseEntity.ok(service.get(sectionName));
    }

    @PostMapping("/yearSections")
    public ResponseEntity<YearSection> createYearSection(@RequestBody YearSection yearSection) {
        return ResponseEntity.ok(service.create(yearSection));
    }

    @PutMapping("/yearSections/{sectionName}")
    public ResponseEntity<YearSection> updateYearSection(@RequestBody YearSection yearSection,
                                                         @PathVariable("sectionName") String sectionName) {
        return ResponseEntity.ok(service.update(yearSection, sectionName));
    }

    @DeleteMapping("/yearSections/{sectionName}")
    public ResponseEntity<YearSection> deleteYearSection(@PathVariable("sectionName") String sectionName) {
        service.delete(sectionName);
        return ResponseEntity.ok().build();
    }
}

package com.eze.backend.restapi.controller;

import com.eze.backend.restapi.model.YearLevel;
import com.eze.backend.restapi.service.YearLevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class YearLevelController {

    @Autowired
    private YearLevelService service;

    @GetMapping("/yearLevels")
    public ResponseEntity<List<YearLevel>> getYearLevels() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/yearLevels/{yearNumber}")
    public ResponseEntity<YearLevel> getYearLevel(@PathVariable("yearNumber") String yearNumber) {
        return ResponseEntity.ok(service.get(yearNumber));
    }

    @PostMapping("/yearLevels")
    public ResponseEntity<YearLevel> createYearLevel(@RequestBody YearLevel yearLevel) {
        return ResponseEntity.ok(service.create(yearLevel));
    }

    @PutMapping("/yearLevels/{yearNumber}")
    public ResponseEntity<YearLevel> updateYearLevel(@RequestBody YearLevel yearLevel,
                                                     @PathVariable("yearNumber") String yearNumber) {
        return ResponseEntity.ok(service.update(yearLevel, yearNumber));
    }

    @DeleteMapping("/yearLevels/{yearNumber}")
    public ResponseEntity<YearLevel> deleteYearLevel(@PathVariable("yearNumber") String yearNumber) {
        service.delete(yearNumber);
        return ResponseEntity.ok().build();
    }
}

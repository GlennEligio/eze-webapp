package com.eze.backend.restapi.controller;

import com.eze.backend.restapi.dtos.YearLevelDto;
import com.eze.backend.restapi.dtos.YearLevelWithSectionsDto;
import com.eze.backend.restapi.model.YearLevel;
import com.eze.backend.restapi.service.YearLevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class YearLevelController {

    @Autowired
    private YearLevelService service;

    @GetMapping("/yearLevels")
    public ResponseEntity<List<YearLevelWithSectionsDto>> getYearLevels() {
        return ResponseEntity.ok(service.getAll().stream().map(YearLevel::toYearLevelWithSectionsDto).toList());
    }

    @GetMapping("/yearLevels/{yearNumber}")
    public ResponseEntity<YearLevelWithSectionsDto> getYearLevel(@PathVariable("yearNumber") String yearNumber) {
        return ResponseEntity.ok(YearLevel.toYearLevelWithSectionsDto(service.get(yearNumber)));
    }

    @PostMapping("/yearLevels")
    public ResponseEntity<YearLevelDto> createYearLevel(@Valid @RequestBody YearLevelDto dto) {
        return ResponseEntity.ok(YearLevel.toYearLevelDto(service.create(YearLevel.toYearLevel(dto))));
    }

    @PutMapping("/yearLevels/{yearNumber}")
    public ResponseEntity<YearLevelWithSectionsDto> updateYearLevel(@Valid @RequestBody YearLevelDto dto,
                                                     @PathVariable("yearNumber") String yearNumber) {
        return ResponseEntity.ok(YearLevel.toYearLevelWithSectionsDto(service.update(YearLevel.toYearLevel(dto), yearNumber)));
    }

    @DeleteMapping("/yearLevels/{yearNumber}")
    public ResponseEntity<Object> deleteYearLevel(@PathVariable("yearNumber") String yearNumber) {
        service.delete(yearNumber);
        return ResponseEntity.ok().build();
    }
}

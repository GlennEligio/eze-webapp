package com.eze.backend.spring.controller;

import com.eze.backend.spring.dtos.YearLevelDto;
import com.eze.backend.spring.dtos.YearLevelWithSectionsDto;
import com.eze.backend.spring.exception.ApiException;
import com.eze.backend.spring.model.YearLevel;
import com.eze.backend.spring.repository.YearLevelRepository;
import com.eze.backend.spring.service.YearLevelService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1")
@Slf4j
public class YearLevelController {

    @Autowired
    private YearLevelService service;

    @GetMapping("/yearLevels")
    public ResponseEntity<List<YearLevelWithSectionsDto>> getYearLevels() {
        return ResponseEntity.ok(service.getAllNotDeleted().stream().map(YearLevel::toYearLevelWithSectionsDto).toList());
    }

    @GetMapping("/yearLevels/download")
    public void download(HttpServletResponse response) throws IOException {
        log.info("Preparing Year level list for Download");
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=yearLevels.xlsx");
        ByteArrayInputStream stream = service.listToExcel(service.getAll());
        IOUtils.copy(stream, response.getOutputStream());
    }

    @PostMapping("/yearLevels/upload")
    public ResponseEntity<Object> upload(@RequestParam(required = false, defaultValue = "false") Boolean overwrite,
                                         @RequestParam MultipartFile file) {
        log.info("Preparing Excel for Year level Database update");
        if(!Objects.equals(file.getContentType(), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")){
            throw new ApiException("Can only upload .xlsx files", HttpStatus.BAD_REQUEST);
        }
        List<YearLevel> yearLevels = service.excelToList(file);
        log.info("Got the yearLevels from excel");
        int itemsAffected = service.addOrUpdate(yearLevels, overwrite);
        log.info("Successfully updated {} yearLevels database using the excel file", itemsAffected);
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("Items Affected", itemsAffected);
        return ResponseEntity.ok(objectNode);
    }

    @GetMapping("/yearLevels/{yearNumber}")
    public ResponseEntity<YearLevelWithSectionsDto> getYearLevel(@PathVariable("yearNumber") Integer yearNumber) {
        YearLevel yearLevel = service.get(yearNumber);
        YearLevelWithSectionsDto yearLevelWithSectionsDto = YearLevel.toYearLevelWithSectionsDto(yearLevel);
        return ResponseEntity.ok(yearLevelWithSectionsDto);
    }

    @PostMapping("/yearLevels")
    public ResponseEntity<YearLevelDto> createYearLevel(@Valid @RequestBody YearLevelDto dto) {
        YearLevel yearLevel = service.create(YearLevel.toYearLevel(dto));
        YearLevelDto yearLevelDto = YearLevel.toYearLevelDto(yearLevel);
        return ResponseEntity.status(HttpStatus.CREATED).body(yearLevelDto);
    }

    @PutMapping("/yearLevels/{yearNumber}")
    public ResponseEntity<YearLevelWithSectionsDto> updateYearLevel(@Valid @RequestBody YearLevelDto dto,
                                                     @PathVariable("yearNumber") Integer yearNumber) {
        YearLevel yearLevel = YearLevel.toYearLevel(dto);
        YearLevel updatedYearLevel = service.update(yearLevel, yearNumber);
        YearLevelWithSectionsDto yearLevelWithSectionsDto = YearLevel.toYearLevelWithSectionsDto(updatedYearLevel);
        return ResponseEntity.ok(yearLevelWithSectionsDto);
    }

    @DeleteMapping("/yearLevels/{yearNumber}")
    public ResponseEntity<Object> deleteYearLevel(@PathVariable("yearNumber") Integer yearNumber) {
        service.softDelete(yearNumber);
        return ResponseEntity.ok().build();
    }
}

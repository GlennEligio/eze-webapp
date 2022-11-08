package com.eze.backend.restapi.controller;

import com.eze.backend.restapi.dtos.YearSectionDto;
import com.eze.backend.restapi.dtos.YearSectionWithYearLevelDto;
import com.eze.backend.restapi.exception.ApiException;
import com.eze.backend.restapi.model.YearLevel;
import com.eze.backend.restapi.model.YearSection;
import com.eze.backend.restapi.service.YearSectionService;
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
public class YearSectionController {

    @Autowired
    private YearSectionService service;

    @GetMapping("/yearSections")
    public ResponseEntity<List<YearSectionDto>> getYearSections() {
        return ResponseEntity.ok(service.getAllNotDeleted().stream().map(YearSection::toYearSectionDto).toList());
    }

    @GetMapping("/yearSections/download")
    public void download(HttpServletResponse response) throws IOException {
        log.info("Preparing Year section list for Download");
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=yearSections.xlsx");
        ByteArrayInputStream stream = service.listToExcel(service.getAll());
        IOUtils.copy(stream, response.getOutputStream());
    }

    @PostMapping("/yearSections/upload")
    public ResponseEntity<Object> upload(@RequestParam(required = false, defaultValue = "false") Boolean overwrite,
                                         @RequestParam MultipartFile file) {
        log.info("Preparing Excel for Year level Database update");
        if(!Objects.equals(file.getContentType(), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")){
            throw new ApiException("Can only upload .xlsx files", HttpStatus.BAD_REQUEST);
        }
        List<YearSection> yearLevels = service.excelToList(file);
        log.info("Got the yearSections from excel");
        int itemsAffected = service.addOrUpdate(yearLevels, overwrite);
        log.info("Successfully updated {} yearSections database using the excel file", itemsAffected);
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("Year sections Affected", itemsAffected);
        return ResponseEntity.ok(objectNode);
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
        service.softDelete(sectionName);
        return ResponseEntity.ok().build();
    }
}

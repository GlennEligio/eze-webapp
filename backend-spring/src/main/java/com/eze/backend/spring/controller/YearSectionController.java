package com.eze.backend.spring.controller;

import com.eze.backend.spring.dtos.YearSectionDto;
import com.eze.backend.spring.dtos.YearSectionWithYearLevelDto;
import com.eze.backend.spring.exception.ApiException;
import com.eze.backend.spring.model.YearLevel;
import com.eze.backend.spring.model.YearSection;
import com.eze.backend.spring.service.YearSectionService;
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
        List<YearSectionDto> yearSectionDtoList = service.getAllNotDeleted().stream().map(YearSection::toYearSectionDto).toList();
        return ResponseEntity.ok(yearSectionDtoList);
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
        List<YearSection> yearSections = service.excelToList(file);
        log.info("Got the yearSections from excel");
        int itemsAffected = service.addOrUpdate(yearSections, overwrite);
        log.info("Successfully updated {} yearSections database using the excel file", itemsAffected);
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();
        // TODO: Edit the FRE to read the correct property in response
        objectNode.put("Items Affected", itemsAffected);
        return ResponseEntity.ok(objectNode);
    }

    @GetMapping("/yearSections/{sectionName}")
    public ResponseEntity<YearSectionWithYearLevelDto> getYearSection(@PathVariable("sectionName") String sectionName) {
        YearSection yearSection = service.get(sectionName);
        YearSectionWithYearLevelDto yearSectionWithYearLevelDto = YearSection.toYearSectionWithYearLevelDto(yearSection);
        return ResponseEntity.ok(yearSectionWithYearLevelDto);
    }

    @PostMapping("/yearSections")
    public ResponseEntity<YearSectionWithYearLevelDto> createYearSection(@Valid @RequestBody YearSectionWithYearLevelDto dto) {
        YearSection ysToCreate = YearSection.toYearSection(dto);
        YearSection newYearSection = service.create(ysToCreate);
        YearSectionWithYearLevelDto yearSectionWithYearLevelDto = YearSection.toYearSectionWithYearLevelDto(newYearSection);
        log.info(yearSectionWithYearLevelDto.toString());
        return ResponseEntity.status(HttpStatus.CREATED).body(yearSectionWithYearLevelDto);
    }

    @PutMapping("/yearSections/{sectionName}")
    public ResponseEntity<YearSectionWithYearLevelDto> updateYearSection(@Valid @RequestBody YearSectionWithYearLevelDto dto,
                                                         @PathVariable("sectionName") String sectionName) {
        YearSection yearSectionForUpdate = YearSection.toYearSection(dto);
        YearSection updatedYearSection = service.update(yearSectionForUpdate, sectionName);
        YearSectionWithYearLevelDto yearSectionWithYearLevelDto = YearSection.toYearSectionWithYearLevelDto(updatedYearSection);
        return ResponseEntity.ok(yearSectionWithYearLevelDto);
    }

    @DeleteMapping("/yearSections/{sectionName}")
    public ResponseEntity<YearSection> deleteYearSection(@PathVariable("sectionName") String sectionName) {
        service.softDelete(sectionName);
        return ResponseEntity.ok().build();
    }
}

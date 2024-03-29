package com.eze.backend.spring.controller;

import com.eze.backend.spring.dtos.ProfessorDto;
import com.eze.backend.spring.model.Professor;
import com.eze.backend.spring.exception.ApiException;
import com.eze.backend.spring.service.ProfessorService;
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
@Slf4j
@RequestMapping("/api/v1")
public class ProfessorController {

    @Autowired
    private ProfessorService service;

    @GetMapping("/professors")
    public ResponseEntity<List<ProfessorDto>> getProfessors(@RequestParam(required = false) String name) {
        log.info("Search professor by name {}", name);
        List<Professor> professors = service.getAllNotDeleted();
        if(name != null && !name.isBlank()) {
            professors = professors.stream().filter(p -> p.getName().toLowerCase().contains(name.toLowerCase())).toList();
        }
        List<ProfessorDto> professorDtoList = professors.stream().map(Professor::toProfessorDto).toList();
        return ResponseEntity.ok(professorDtoList);
    }

    @GetMapping("/professors/download")
    public void download(HttpServletResponse response) throws IOException {
        log.info("Preparing Professors list for Download");
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=professors.xlsx");
        ByteArrayInputStream stream = service.listToExcel(service.getAll());
        IOUtils.copy(stream, response.getOutputStream());
    }

    @PostMapping("/professors/upload")
    public ResponseEntity<Object> upload(@RequestParam(required = false, defaultValue = "false") Boolean overwrite,
                                         @RequestParam MultipartFile file) {
        log.info("Preparing Excel for Professor Database update");
        if(!Objects.equals(file.getContentType(), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")){
            throw new ApiException("Can only upload .xlsx files", HttpStatus.BAD_REQUEST);
        }
        List<Professor> professors = service.excelToList(file);
        log.info("Got the professors from excel");
        int itemsAffected = service.addOrUpdate(professors, overwrite);
        log.info("Successfully updated {} professors database using the excel file", itemsAffected);
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("Professors Affected", itemsAffected);
        return ResponseEntity.ok(objectNode);
    }

    @GetMapping("/professors/{name}")
    public ResponseEntity<ProfessorDto> getProfessor(@PathVariable("name") String name) {
        Professor professor =service.get(name);
        ProfessorDto professorDto = Professor.toProfessorDto(professor);
        return ResponseEntity.ok(professorDto);
    }

    @PostMapping("/professors")
    public ResponseEntity<ProfessorDto> createProfessor(@Valid @RequestBody ProfessorDto dto) {
        Professor professorToCreate = Professor.toProfessor(dto);
        Professor createdProfessor = service.create(professorToCreate);
        ProfessorDto dtoResponse = Professor.toProfessorDto(createdProfessor);
        return ResponseEntity.status(HttpStatus.CREATED).body(dtoResponse);
    }

    @PutMapping("/professors/{name}")
    public ResponseEntity<ProfessorDto> updateProfessor(@Valid @RequestBody ProfessorDto dto,
                                                     @PathVariable("name") String name) {
        Professor professorForUpdate = Professor.toProfessor(dto);
        Professor updatedProfessor = service.update(professorForUpdate, name);
        ProfessorDto dtoResponse = Professor.toProfessorDto(updatedProfessor);
        return ResponseEntity.ok(dtoResponse);
    }

    @DeleteMapping("/professors/{name}")
    public ResponseEntity<Professor> deleteProfessor(@PathVariable("name") String name) {
        service.softDelete(name);
        return ResponseEntity.ok().build();
    }
}

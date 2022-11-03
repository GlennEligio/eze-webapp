package com.eze.backend.restapi.controller;

import com.eze.backend.restapi.dtos.ProfessorDto;
import com.eze.backend.restapi.model.Professor;
import com.eze.backend.restapi.service.ProfessorService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ProfessorController {

    @Autowired
    private ProfessorService service;

    @GetMapping("/professors")
    public ResponseEntity<List<ProfessorDto>> getProfessors() {
        return ResponseEntity.ok(service.getAll().stream().map(Professor::toProfessorDto).toList());
    }

    @GetMapping("/professors/{name}")
    public ResponseEntity<ProfessorDto> getProfessor(@PathVariable("name") String name) {
        return ResponseEntity.ok(Professor.toProfessorDto(service.get(name)));
    }

    @PostMapping("/professors")
    public ResponseEntity<ProfessorDto> createProfessor(@Valid @RequestBody ProfessorDto dto) {
        return ResponseEntity.status(201).body(Professor.toProfessorDto(service.create(Professor.toProfessor(dto))));
    }

    @PutMapping("/professors/{name}")
    public ResponseEntity<ProfessorDto> updateProfessor(@Valid @RequestBody ProfessorDto dto,
                                                     @PathVariable("name") String name) {
        return ResponseEntity.ok(Professor.toProfessorDto(service.update(Professor.toProfessor(dto), name)));
    }

    @DeleteMapping("/professors/{name}")
    public ResponseEntity<Professor> deleteProfessor(@PathVariable("name") String name) {
        service.delete(name);
        return ResponseEntity.ok().build();
    }
}

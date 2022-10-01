package com.eze.backend.restapi.controller;

import com.eze.backend.restapi.model.Professor;
import com.eze.backend.restapi.service.ProfessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ProfessorController {

    @Autowired
    private ProfessorService service;

    @GetMapping("/professors")
    public ResponseEntity<List<Professor>> getProfessors() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/professors/{name}")
    public ResponseEntity<Professor> getProfessor(@PathVariable("name") String name) {
        return ResponseEntity.ok(service.get(name));
    }

    @PostMapping("/professors")
    public ResponseEntity<Professor> createProfessor(@RequestBody Professor professor) {
        return ResponseEntity.status(201).body(service.create(professor));
    }

    @PutMapping("/professors/{name}")
    public ResponseEntity<Professor> updateProfessor(@RequestBody Professor professor,
                                                     @PathVariable("name") String name) {
        return ResponseEntity.ok(service.update(professor, name));
    }

    @DeleteMapping("/professors/{name}")
    public ResponseEntity<Professor> deleteProfessor(@PathVariable("name") String name) {
        service.delete(name);
        return ResponseEntity.ok().build();
    }
}

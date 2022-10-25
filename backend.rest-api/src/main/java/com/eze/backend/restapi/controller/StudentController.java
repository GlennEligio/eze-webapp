package com.eze.backend.restapi.controller;

import com.eze.backend.restapi.dtos.StudentDto;
import com.eze.backend.restapi.dtos.StudentListDto;
import com.eze.backend.restapi.model.Student;
import com.eze.backend.restapi.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class StudentController {

    @Autowired
    private StudentService service;

    @GetMapping("/students")
    public ResponseEntity<List<StudentListDto>> getStudents(@RequestParam String complete) {
        return ResponseEntity.ok(service.getAll().stream().map(Student::toStudentListDto).toList());
    }

    @GetMapping("/students/{studentNo}")
    public ResponseEntity<Object> getStudent(@PathVariable("studentNo") String studentNo,
                                                 @RequestParam String full) {
        if(full.equalsIgnoreCase("true")) {
            return ResponseEntity.ok(Student.)
        }
        return ResponseEntity.ok(Student.toStudentListDto(service.get(studentNo)));
    }

    @PostMapping("/students")
    public ResponseEntity<StudentDto> createStudent(@RequestBody Student student) {
        return ResponseEntity.status(HttpStatus.CREATED).body(Student.toStudentListDto(service.create(student)));
    }

    @PutMapping("/students/{studentNo}")
    public ResponseEntity<StudentDto> updateStudent(@RequestBody Student student,
                                                 @PathVariable("studentNo") String studentNo) {
        return ResponseEntity.ok(Student.toStudentListDto(service.update(student, studentNo)));
    }

    @DeleteMapping("/students/{studentNo}")
    public ResponseEntity<Object> deleteStudent(@PathVariable("studentNo") String studentNo) {
        service.delete(studentNo);
        return ResponseEntity.ok().build();
    }
}

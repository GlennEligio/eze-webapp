package com.eze.backend.restapi.controller;

import com.eze.backend.restapi.dtos.StudentDto;
import com.eze.backend.restapi.model.Student;
import com.eze.backend.restapi.service.StudentService;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<List<StudentDto>> getStudents() {
        return ResponseEntity.ok(service.getAll().stream().map(Student::toStudentDto).toList());
    }

    @GetMapping("/students/{studentNo}")
    public ResponseEntity<StudentDto> getStudent(@PathVariable("studentNo") String studentNo) {
        return ResponseEntity.ok(Student.toStudentDto(service.get(studentNo)));
    }

    @PostMapping("/students")
    public ResponseEntity<StudentDto> createStudent(@RequestBody Student student) {
        return ResponseEntity.status(HttpStatus.CREATED).body(Student.toStudentDto(service.create(student)));
    }

    @PutMapping("/students/{studentNo}")
    public ResponseEntity<StudentDto> updateStudent(@RequestBody Student student,
                                                 @PathVariable("studentNo") String studentNo) {
        return ResponseEntity.ok(Student.toStudentDto(service.update(student, studentNo)));
    }

    @DeleteMapping("/students/{studentNo}")
    public ResponseEntity<Object> deleteStudent(@PathVariable("studentNo") String studentNo) {
        service.delete(studentNo);
        return ResponseEntity.ok().build();
    }
}

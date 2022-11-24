package com.eze.backend.spring.controller;

import com.eze.backend.spring.dtos.StudentDto;
import com.eze.backend.spring.dtos.StudentListDto;
import com.eze.backend.spring.model.Student;
import com.eze.backend.spring.exception.ApiException;
import com.eze.backend.spring.service.StudentService;
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
public class StudentController {

    @Autowired
    private StudentService service;

    @GetMapping("/students")
    public ResponseEntity<List<StudentListDto>> getStudents() {
        List<Student> students = service.getAllNotDeleted();
        List<StudentListDto> studentListDtos = students.stream().map(Student::toStudentListDto).toList();
        return ResponseEntity.ok(studentListDtos);
    }

    @GetMapping("/students/download")
    public void download(HttpServletResponse response) throws IOException {
        log.info("Preparing Student list for Download");
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=students.xlsx");
        ByteArrayInputStream stream = service.listToExcel(service.getAll());
        IOUtils.copy(stream, response.getOutputStream());
    }

    @PostMapping("/students/upload")
    public ResponseEntity<Object> upload(@RequestParam(required = false, defaultValue = "false") Boolean overwrite,
                                         @RequestParam MultipartFile file) {
        log.info("Preparing Excel for Student Database update");
        if(!Objects.equals(file.getContentType(), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")){
            throw new ApiException("Can only upload .xlsx files", HttpStatus.BAD_REQUEST);
        }
        List<Student> students = service.excelToList(file);
        log.info("Got the students from excel");
        int itemsAffected = service.addOrUpdate(students, overwrite);
        log.info("Updated {} students database using the excel file", itemsAffected);
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("Students Affected", itemsAffected);
        return ResponseEntity.ok(objectNode);
    }

    @GetMapping("/students/{studentNo}")
    public ResponseEntity<Object> getStudent(@PathVariable("studentNo") String studentNo,
                                                 @RequestParam(required = false, defaultValue = "true") boolean complete) {
        Student student = service.get(studentNo);
        if(complete) {
            return ResponseEntity.ok(Student.toStudentDto(student));
        }
        return ResponseEntity.ok(Student.toStudentListDto(student));
    }

    @PostMapping("/students")
    public ResponseEntity<Object> createStudent(@Valid @RequestBody StudentDto dto,
                                                    @RequestParam(required = false, defaultValue = "true") boolean complete) {
        Student studentToCreate = Student.toStudent(dto);
        Student studentCreated = service.create(studentToCreate);
        if(complete){
            StudentDto studentDto = Student.toStudentDto(studentCreated);
            return ResponseEntity.status(HttpStatus.CREATED).body(studentDto);
        }
        StudentListDto studentListDto = Student.toStudentListDto(studentCreated);
        return ResponseEntity.status(HttpStatus.CREATED).body(studentListDto);
    }

    @PutMapping("/students/{studentNo}")
    public ResponseEntity<Object> updateStudent(@Valid @RequestBody StudentDto dto,
                                                 @PathVariable("studentNo") String studentNo,
                                                    @RequestParam(required = false, defaultValue = "true") boolean complete) {
        Student studentForUpdate = Student.toStudent(dto);
        Student updatedStudent = service.update(studentForUpdate, studentNo);
        if(complete) {
            StudentDto studentDto = Student.toStudentDto(updatedStudent);
            return ResponseEntity.ok(studentDto);
        }
        StudentListDto studentListDto = Student.toStudentListDto(updatedStudent);
        return ResponseEntity.ok(studentListDto);
    }

    @DeleteMapping("/students/{studentNo}")
    public ResponseEntity<Object> deleteStudent(@PathVariable("studentNo") String studentNo) {
        service.softDelete(studentNo);
        return ResponseEntity.ok().build();
    }
}

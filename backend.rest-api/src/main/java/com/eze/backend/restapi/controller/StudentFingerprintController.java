//package com.eze.backend.restapi.controller;
//
//import com.eze.backend.restapi.model.StudentFingerprint;
//import com.eze.backend.restapi.service.StudentFingerprintService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//// TODO: Implement biometric authenticatio using DFPF api alongside biometric validation (DFPF api) to properly validate fingerprint
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/api/v1")
//public class StudentFingerprintController {
//
//    private StudentFingerprintService service;
//
//    @GetMapping("/studentFingerprints")
//    public ResponseEntity<List<StudentFingerprint>> getStudentFingerprints() {
//        return ResponseEntity.ok(service.getAll());
//    }
//
//    @GetMapping("/studentFingerprints/{studentNumber}")
//    public ResponseEntity<StudentFingerprint> getStudentFingerprint(@PathVariable("studentNumber") String studentNumber) {
//        return ResponseEntity.ok(service.get(studentNumber));
//    }
//
//    @PostMapping("/studentFingerprints")
//    public ResponseEntity<StudentFingerprint> createStudentFingerprint(@RequestBody StudentFingerprint sf) {
//        return ResponseEntity.status(HttpStatus.CREATED).body(sf);
//    }
//
//    @PutMapping("/studentFingerprints/{studentNumber}")
//    public ResponseEntity<StudentFingerprint> updateStudentFingerprint(@RequestBody StudentFingerprint sf,
//                                                                       @PathVariable("studentNumber") String studentNumber) {
//        return ResponseEntity.ok(service.update(sf, studentNumber));
//    }
//
//    @DeleteMapping("/studentFingerprints/{studentNumber}")
//    public ResponseEntity<Object> deleteStudentFingerprint(@PathVariable("studentNumber") String studentNumber) {
//        service.delete(studentNumber);
//        return ResponseEntity.ok().build();
//    }
//}

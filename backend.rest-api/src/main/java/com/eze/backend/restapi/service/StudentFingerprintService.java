package com.eze.backend.restapi.service;

import com.eze.backend.restapi.exception.ApiException;
import com.eze.backend.restapi.model.Account;
import com.eze.backend.restapi.model.AccountFingerprint;
import com.eze.backend.restapi.model.Student;
import com.eze.backend.restapi.model.StudentFingerprint;
import com.eze.backend.restapi.repository.StudentFingerprintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Service
public class StudentFingerprintService implements IService<StudentFingerprint>{

    @Autowired
    private StudentFingerprintRepository repository;
    @Autowired
    private StudentService studentService;

    @Override
    public List<StudentFingerprint> getAll() {
        return repository.findAll();
    }

    @Override
    public StudentFingerprint get(Serializable studentNumber) {
        return repository.findByStudentNumber(studentNumber.toString())
                .orElseThrow(() -> new ApiException(notFound(studentNumber), HttpStatus.NOT_FOUND));
    }

    @Override
    public StudentFingerprint create(StudentFingerprint sf) {
        if (sf.getStudent() != null && sf.getStudent().getStudentNumber() != null) {
            // Check if student with studentNumber exist
            Student student = studentService.get(sf.getStudent().getStudentNumber());
            // If it exists, check a fingerprint already exist for the student
            if (student.getStudentFingerprint() != null) {
                // If fingerprint already present, cancel save
                throw new ApiException(alreadyExist(student.getStudentNumber()), HttpStatus.BAD_REQUEST);
            }
            // Else save fingerprint alongside the student
            sf.setStudent(student);
            return repository.save(sf);
        }
        // cancel fingerprint save, no student attached to the fingerprint
        throw new ApiException("No student is attached to fingerprint", HttpStatus.BAD_REQUEST);
    }

    @Override
    public StudentFingerprint update(StudentFingerprint sf, Serializable studentNumber) {
        // Using studentNumber, check if a student exist
        Student student = studentService.get(studentNumber);
        // update the fingerprint data in the student
        StudentFingerprint sfp = student.getStudentFingerprint();
        sfp.setFingerprint(sf.getFingerprint());
        return repository.save(sfp);
    }

    @Override
    public void delete(Serializable studentNumber) {
        // Using accountCode, find the account
        Student student = studentService.get(studentNumber);
        // Set the fingerprint to null
        StudentFingerprint sf = student.getStudentFingerprint();
        repository.delete(sf);
    }

    @Override
    public String notFound(Serializable studentNumber) {
        return "StudentFingerprint for student number " + studentNumber + " does not exist";
    }

    @Override
    public String alreadyExist(Serializable studentNumber) {
        return "StudentFingerprint for student number " + studentNumber + " already exist";
    }
}

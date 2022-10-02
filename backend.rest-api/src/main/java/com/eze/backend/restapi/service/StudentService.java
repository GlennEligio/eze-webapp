package com.eze.backend.restapi.service;

import com.eze.backend.restapi.exception.ApiException;
import com.eze.backend.restapi.model.Student;
import com.eze.backend.restapi.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Service
public class StudentService implements IService<Student>{

    @Autowired
    private StudentRepository repository;

    @Override
    public List<Student> getAll() {
        return repository.findAll();
    }

    @Override
    public Student get(Serializable studentNo) {
        return repository.findByStudentNumber(studentNo.toString()).orElseThrow(() -> new ApiException(notFound(studentNo), HttpStatus.NOT_FOUND));
    }

    @Override
    public Student create(Student student) {
        Optional<Student> studentOp = repository.findByStudentNumber(student.getStudentNumber());
        if(studentOp.isPresent()) {
            throw new ApiException(alreadyExist(student.getStudentNumber()), HttpStatus.BAD_REQUEST);
        }
        return repository.save(student);
    }

    @Override
    public Student update(Student student, Serializable studentNo) {
        Student student1 = repository.findByStudentNumber(studentNo.toString())
                .orElseThrow(() -> new ApiException(notFound(studentNo), HttpStatus.NOT_FOUND));
        student1.update(student);
        return repository.save(student1);
    }

    @Override
    public void delete(Serializable studentNo) {
        Student student = repository.findByStudentNumber(studentNo.toString())
                .orElseThrow(() -> new ApiException(notFound(studentNo), HttpStatus.NOT_FOUND));
        repository.delete(student);
    }

    @Override
    public String notFound(Serializable studentNo) {
        return "No student with student number " + studentNo + " was found";
    }

    @Override
    public String alreadyExist(Serializable studentNo) {
        return "Student with student number " + studentNo + " already exist";
    }
}

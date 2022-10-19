package com.eze.backend.restapi.service;

import com.eze.backend.restapi.exception.ApiException;
import com.eze.backend.restapi.model.Student;
import com.eze.backend.restapi.model.YearLevel;
import com.eze.backend.restapi.model.YearSection;
import com.eze.backend.restapi.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class StudentService implements IService<Student>{

    private final StudentRepository repository;
    private final YearLevelService ylService;
    private final YearSectionService ysService;

    public StudentService(StudentRepository repository, YearLevelService ylService, YearSectionService ysService) {
        this.repository = repository;
        this.ylService = ylService;
        this.ysService = ysService;
    }

    @Override
    public List<Student> getAll() {
        log.info("Fetching all students");
        return repository.findAll();
    }

    @Override
    public Student get(Serializable studentNo) {
        log.info("Fetching student with student number {}", studentNo);
        return repository.findByStudentNumber(studentNo.toString()).orElseThrow(() -> new ApiException(notFound(studentNo), HttpStatus.NOT_FOUND));
    }

    @Override
    public Student create(Student student) {
        log.info("Creating student {}", student);
        Optional<Student> studentOp = repository.findByStudentNumber(student.getStudentNumber());
        if(studentOp.isPresent()) {
            throw new ApiException(alreadyExist(student.getStudentNumber()), HttpStatus.BAD_REQUEST);
        }
        YearLevel yearLevel = ylService.get(student.getYearLevel().getYearNumber());
        YearSection yearSection = ysService.get(student.getYearAndSection().getSectionName());
        student.setYearLevel(yearLevel);
        student.setYearAndSection(yearSection);

        return repository.save(student);
    }

    @Override
    public Student update(Student student, Serializable studentNo) {
        log.info("Updating student {} with student number {}", student, studentNo);
        Student student1 = repository.findByStudentNumber(studentNo.toString())
                .orElseThrow(() -> new ApiException(notFound(studentNo), HttpStatus.NOT_FOUND));
        YearLevel yearLevel = ylService.get(student.getYearLevel().getYearNumber());
        YearSection yearSection = ysService.get(student.getYearAndSection().getSectionName());
        student.setYearLevel(yearLevel);
        student.setYearAndSection(yearSection);
        student1.update(student);
        return repository.save(student1);
    }

    @Override
    public void delete(Serializable studentNo) {
        log.info("Deleting student with student number {}", studentNo);
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

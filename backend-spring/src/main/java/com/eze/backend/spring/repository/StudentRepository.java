package com.eze.backend.spring.repository;

import com.eze.backend.spring.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByStudentNumber(String studentNumber);

    @Query( "SELECT s FROM Student s WHERE s.deleteFlag=false")
    List<Student> findAllNotDeleted();

    //Soft delete.
    @Query("UPDATE Student s SET s.deleteFlag=true WHERE s.studentNumber=?1")
    @Modifying
    void softDelete(String studentNumber);
}

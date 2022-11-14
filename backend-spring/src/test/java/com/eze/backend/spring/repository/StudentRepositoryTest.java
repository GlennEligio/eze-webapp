package com.eze.backend.spring.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.eze.backend.spring.model.Student;
import com.eze.backend.spring.model.YearLevel;
import com.eze.backend.spring.model.YearSection;
import com.eze.backend.spring.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

@DataJpaTest
class StudentRepositoryTest {

    @Autowired
    private StudentRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private Student student0;

    @BeforeEach
    void setup() {
        YearLevel yearLevel = new YearLevel(1, "First", false);
        YearSection yearSection = new YearSection("SectionName1", false, yearLevel);
        student0 = new Student("2015-00129-MN-00", "FullName0", yearSection, "09062560574", "Birthday0", "Address0", "Email0", "Guardian0", "GuardianNumber0", yearLevel, "https://sampleprofile0.com", false);
        Student student1 = new Student("2015-00129-MN-01", "FullName1", yearSection, "09062560571", "Birthday1", "Address1", "Email1", "Guardian1", "GuardianNumber1", yearLevel, "https://sampleprofile1.com", true);
        entityManager.persist(yearLevel);
        entityManager.persist(yearSection);
        entityManager.persist(student0);
        entityManager.persist(student1);
    }

    @Test
    @DisplayName("Find Student using valid Student number")
    void findByStudentNumber_usingValidStudentNumber_returnsStudent() {
        String validStudentNumber = "2015-00129-MN-00";

        Optional<Student> studentOptional = repository.findByStudentNumber(validStudentNumber);

        assertTrue(studentOptional.isPresent());
        assertEquals(studentOptional.get(), student0);
    }

    @Test
    @DisplayName("Find Student using invalid Student number")
    void findByStudentNumber_usingInvalidStudentNumber_returnEmpty() {
        String invalidStudentNumber = "2015-99999-MN-99";

        Optional<Student> studentOptional = repository.findByStudentNumber(invalidStudentNumber);

        assertTrue(studentOptional.isEmpty());
    }

    @Test
    @DisplayName("Find All Not Deleted Students")
    void findAllNotDeletedStudents_returnsNotDeletedStudents () {
        List<Student> studentList = repository.findAllNotDeleted();

        assertEquals(0, studentList.stream().filter(Student::getDeleteFlag).count());
    }

    @Test
    @DisplayName("Soft deletes a Student")
    void softDelete_updatesStudentDeleteFlag() {
        String validStudentNumber = "2015-00129-MN-00";

        repository.softDelete(validStudentNumber);
        Optional<Student> studentOptional = repository.findByStudentNumber(validStudentNumber);

        assertTrue(studentOptional.isPresent());
        assertTrue(studentOptional.get().getDeleteFlag());
    }
}

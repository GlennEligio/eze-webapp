package com.eze.backend.spring.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.eze.backend.spring.model.Professor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

@DataJpaTest
class ProfessorRepositoryTest {

    @Autowired
    private ProfessorRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private Professor professor0;

    @BeforeEach
    void setup() {
        professor0 = new Professor("Name0", "+639062560574", false);
        Professor professor1 = new Professor("Name1", "+639062560574", true);
        entityManager.persist(professor0);
        entityManager.persist(professor1);
    }

    @Test
    @DisplayName("Find Professor by Name using Valid Name")
    void findByName_usingValidName_returnsProfessor() {
        String validName = "Name0";

        Optional<Professor> professorOptional = repository.findByName(validName);

        assertTrue(professorOptional.isPresent());
        assertEquals(professorOptional.get(), professor0);
    }

    @Test
    @DisplayName("Find Professor by Name using invalid Name")
    void findByName_usingInvalidName_returnsEmpty() {
        String invalidName = "invalidName";

        Optional<Professor> professorOptional = repository.findByName(invalidName);

        assertTrue(professorOptional.isEmpty());
    }

    @Test
    @DisplayName("Find All Non deleted Professors")
    void findAllNotDeleted_returnsNotDeleteProfessors() {
        List<Professor> professorList = repository.findAllNotDeleted();

        assertEquals(0, professorList.stream().filter(Professor::getDeleteFlag).count());
    }

    @Test
    @DisplayName("Soft delete a Professor")
    void softDelete_updatesProfessorDeleteFlag() {
        String validName = "Name0";

        repository.softDelete(validName);
        Optional<Professor> professorOptional = repository.findByName(validName);

        assertTrue(professorOptional.isPresent());
        assertTrue(professorOptional.get().getDeleteFlag());
    }
}

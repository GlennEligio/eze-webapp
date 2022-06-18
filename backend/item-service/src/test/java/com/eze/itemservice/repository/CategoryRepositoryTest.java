package com.eze.itemservice.repository;

import static org.junit.jupiter.api.Assertions.*;
import com.eze.itemservice.domain.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

@DataJpaTest
@DisplayName("CategoryRepositoryTest")
public class CategoryRepositoryTest {

    private final TestEntityManager testEntityManager;
    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryRepositoryTest(TestEntityManager testEntityManager, CategoryRepository categoryRepository) {
        this.testEntityManager = testEntityManager;
        this.categoryRepository = categoryRepository;
    }

    @BeforeEach
    void beforeAll() {
        Category cat0 = new Category("KEYS", "KEYS");
        Category cat1 = new Category( "TOOL", "KEYS");
        Category cat2 = new Category("CONSUMABLE", "KEYS");
        List<Category> categories = List.of(cat0, cat1, cat2);
        categories.forEach(testEntityManager::persist);
    }

    @DisplayName("fetch a category with valid code and returns an Optional with Category with same code")
    @Test
    void findByCategoryCode_withValidCode_returnsOptionalWithCategory() {
        String validCode = "KEYS";
        Optional<Category> catOp = categoryRepository.findByCategoryCode(validCode);

        assertTrue(catOp.isPresent());
        assertEquals(validCode, catOp.get().getCategoryCode());
    }

    @DisplayName("fetch a category with invalid code and returns an empty Optional")
    @Test
    void findByCategoryCode_withInvalidCode_returnsEmptyOptional() {
        String invalidCode = "INVALID";
        Optional<Category> catOp = categoryRepository.findByCategoryCode(invalidCode);

        assertTrue(catOp.isEmpty());
    }
}

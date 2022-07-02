package com.eze.itemservice.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.eze.itemservice.domain.Category;
import com.eze.itemservice.domain.Item;
import org.antlr.stringtemplate.language.Cat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@DataJpaTest
@DisplayName("ItemRepositoryTest")
class ItemRepositoryTest {

    private final TestEntityManager testEntityManager;
    private final ItemRepository itemRepository;

    private Item item0;

    @Autowired
    public ItemRepositoryTest(TestEntityManager testEntityManager, ItemRepository itemRepository) {
        this.testEntityManager = testEntityManager;
        this.itemRepository = itemRepository;
    }

    @BeforeEach
    void setup(){
        Category category = new Category("KEY", "KEY");
        item0 = new Item("itemCode0", BigInteger.valueOf(100), BigInteger.valueOf(100), "description0", category, false);
        Item item1 = new Item("itemCode1", BigInteger.valueOf(200), BigInteger.valueOf(200), "description1", category, false);
        Item item2 = new Item( "itemCode2", BigInteger.valueOf(100), BigInteger.valueOf(100), "description2", category, true);
        List<Item> items = List.of(item0, item1, item2);
        items.forEach(testEntityManager::persist);
    }

    @DisplayName("fetch Items with deleteFlag set to false")
    @Test
    void findByDeleteFlagFalse_withItemsWithDeleteFlagFalse_returnListNotEmpty(){
        List<Item> items = itemRepository.findByDeleteFlagFalse();

        assertNotEquals(0, items.size());
        assertEquals(0L, items.stream().filter(item -> item.getDeleteFlag().equals(true)).count());
    }

    @DisplayName("fetch Items with valid itemCode and returns the Item")
    @Test
    void findByItemCodeAndDeleteFlagFalse_withValidItemCode_returnsItem(){
        String validItemCode = item0.getItemCode();

        Optional<Item> itemOp = itemRepository.findByItemCodeAndDeleteFlagFalse(validItemCode);

        assertTrue(itemOp.isPresent());
        assertEquals(validItemCode, itemOp.get().getItemCode());
    }

    @DisplayName("fetch Items with invalid itemCode and returns no Item")
    @Test
    void findByItemCodeAndDeleteFlagFalse_withInvalidItemCode_returnsItem(){
        String invalidItemCode = "invalidCode";

        Optional<Item> itemOp = itemRepository.findByItemCodeAndDeleteFlagFalse(invalidItemCode);

        assertTrue(itemOp.isEmpty());
    }

    @DisplayName("fetch Items with valid Category code and returns Items with same category code")
    @Test
    void findItemByCategoryCode_withValidCategoryCode_returnsItemsWithSameCategoryCode() {
        String validCategoryCode = "KEY";

        assertEquals(0, itemRepository.findItemByCategory(validCategoryCode)
                .stream()
                .filter(item -> !item.getCategory().getCategoryCode().equals(validCategoryCode))
                .count());
    }

    @DisplayName("soft deletes an Item with valid itemCode and changes its deleteFlag value")
    @Test
    void softDelete_withValidItemCode_changesItemDeleteFlag(){
        String validItemCode = item0.getItemCode();

        itemRepository.softDelete(validItemCode);

        Optional<Item> itemOp = itemRepository.findByItemCodeAndDeleteFlagFalse(validItemCode);
        assertTrue(itemOp.isEmpty());
    }
}

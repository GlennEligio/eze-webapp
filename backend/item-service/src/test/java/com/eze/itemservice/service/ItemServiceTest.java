package com.eze.itemservice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.eze.itemservice.domain.Category;
import com.eze.itemservice.domain.Item;
import com.eze.itemservice.exception.ApiException;
import com.eze.itemservice.repository.ItemRepository;
import com.google.common.collect.ImmutableCollection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@SpringBootTest
class ItemServiceTest {

    @MockBean
    private ItemRepository itemRepository;

    @Autowired
    private ItemService itemService;

    private Item item0;
    private List<Item> items;

    @BeforeEach
    void setup(){
        Category category = new Category("C1", "KEY");
        item0 = new Item("itemCode0", BigInteger.valueOf(100), BigInteger.valueOf(100), "description0", category, false);
        Item item1 = new Item("itemCode1", BigInteger.valueOf(200), BigInteger.valueOf(200), "description1", category, false);
        Item item2 = new Item( "itemCode2", BigInteger.valueOf(100), BigInteger.valueOf(100), "description2", category, true);
        items = List.of(item0, item1, item2);
    }

    @DisplayName("find Items whose deleteFlag is false and returns the Items")
    @Test
    void findItems_withItemsPresent_returnItems() {
        when(itemRepository.findByDeleteFlagFalse()).thenReturn(items.stream().filter(item -> item.getDeleteFlag().equals(false)).collect(Collectors.toList()));

        List<Item> items = itemService.findItems();

        assertNotNull(items);
        assertNotEquals(0, items.size());
        assertEquals(0, items.stream().filter(item -> item.getDeleteFlag().equals(true)).count());
    }

    @DisplayName("find Item with valid ItemCode and returns Item")
    @Test
    void findItem_withValidItemCode_returnsItem(){
        String validItemCode = item0.getItemCode();
        when(itemRepository.findByItemCodeAndDeleteFlagFalse(validItemCode)).thenReturn(Optional.of(item0));

        assertDoesNotThrow(() -> itemService.findItem(validItemCode));
        assertNotNull(itemService.findItem(validItemCode));
    }

    @DisplayName("find Item with invalid ItemCode and returns Item")
    @Test
    void findItem_withInvalidItemCode_returnsItem(){
        String invalidItemCode = "invalidItemCode";
        when(itemRepository.findByItemCodeAndDeleteFlagFalse(invalidItemCode)).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> itemService.findItem(invalidItemCode));
    }

    @DisplayName("create new Item and return created Item")
    @Test
    void createItem_withNewItem_returnCreatedItem(){
        String newItemCode = item0.getItemCode();
        when(itemRepository.findByItemCodeAndDeleteFlagFalse(newItemCode)).thenReturn(Optional.empty());
        Item createdItem = item0;
        item0.setDeleteFlag(false);
        when(itemRepository.save(createdItem)).thenReturn(createdItem);

        assertDoesNotThrow(() -> itemService.createItem(item0));
        assertNotNull(itemService.createItem(item0));
        assertFalse(itemService.createItem(item0).getDeleteFlag());
    }

    @DisplayName("create existing Item and throw an exception")
    @Test
    void createItem_withExistingItem_throwsException(){
        String existingItemCode = item0.getItemCode();
        when(itemRepository.findByItemCodeAndDeleteFlagFalse(existingItemCode)).thenReturn(Optional.of(item0));

        assertThrows(ApiException.class, () -> itemService.createItem(item0));
    }

    // TODO: Include effects of setter functions in Unit Test
    @DisplayName("update existing Item and returns updated Item")
    @Test
    void updateItem_withExistingItem_returnsUpdatedItem(){
        String existingItemCode = item0.getItemCode();
        when(itemRepository.findByItemCodeAndDeleteFlagFalse(existingItemCode)).thenReturn(Optional.of(item0));
        when(itemRepository.save(item0)).thenReturn(item0);

        assertDoesNotThrow(() -> itemService.updateItem(item0));
        assertNotNull(itemService.updateItem(item0));
    }

    @DisplayName("update non-existing Item and returns updated Item")
    @Test
    void updateItem_withNonExistingItem_returnsUpdatedItem(){
        String nonExistingItemCode = item0.getItemCode();
        when(itemRepository.findByItemCodeAndDeleteFlagFalse(nonExistingItemCode)).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> itemService.updateItem(item0));
    }

    @DisplayName("delete existing Item and returns true")
    @Test
    void deleteItem_withExistingItem_returnsTrue(){
        String existingItemCode = item0.getItemCode();
        when(itemRepository.findByItemCodeAndDeleteFlagFalse(existingItemCode)).thenReturn(Optional.of(item0));

        assertDoesNotThrow(() -> itemService.deleteItem(existingItemCode));
        assertTrue(itemService.deleteItem(existingItemCode));
    }

    @DisplayName("delete non-existing Item and returns true")
    @Test
    void deleteItem_withNonExistingItem_returnsTrue(){
        String nonExistingItemCode = item0.getItemCode();
        when(itemRepository.findByItemCodeAndDeleteFlagFalse(nonExistingItemCode)).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> itemService.deleteItem(nonExistingItemCode));
    }
}

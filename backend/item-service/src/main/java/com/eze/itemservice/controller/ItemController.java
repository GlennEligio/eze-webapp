package com.eze.itemservice.controller;

import com.eze.itemservice.domain.Category;
import com.eze.itemservice.domain.Item;
import com.eze.itemservice.service.CategoryService;
import com.eze.itemservice.service.ItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ItemController {

    private final ItemService itemService;
    private final CategoryService categoryService;

    public ItemController(ItemService itemService, CategoryService categoryService) {
        this.itemService = itemService;
        this.categoryService = categoryService;
    }

    @GetMapping("/items")
    public ResponseEntity<List<Item>> getAllItem(@RequestParam(value = "category", required = false) String category){
        if(category != null) {
            return ResponseEntity.ok(itemService.findItemsByCategory(category));
        }
        return ResponseEntity.ok(itemService.findItems());
    }

    @GetMapping("/items/{itemCode}")
    public ResponseEntity<Item> getItem(@PathVariable("itemCode") String itemCode){
        return ResponseEntity.ok(itemService.findItem(itemCode));
    }

    @PostMapping("/items")
    public ResponseEntity<Item> createItem(@Valid @RequestBody Item item){
        Item newItem = itemService.createItem(item);
        return ResponseEntity.status(HttpStatus.CREATED).body(newItem);
    }

    @PutMapping("/items")
    public ResponseEntity<Object> updateItem(@Valid @RequestBody Item item){
        itemService.updateItem(item);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/items/{itemCode}")
    public ResponseEntity<Object> deleteItem(@PathVariable("itemCode") String itemCode){
        itemService.deleteItem(itemCode);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/items/categories")
    public ResponseEntity<List<Category>> getItemCategories() {
        return ResponseEntity.ok(categoryService.findCategories());
    }
}

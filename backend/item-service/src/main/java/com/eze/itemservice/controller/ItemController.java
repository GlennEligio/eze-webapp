package com.eze.itemservice.controller;

import com.eze.itemservice.domain.Item;
import com.eze.itemservice.service.ItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ItemController {

    private final ItemService service;

    public ItemController(ItemService service) {
        this.service = service;
    }

    @GetMapping("/items")
    public ResponseEntity<List<Item>> getAllItem(){
        return ResponseEntity.ok(service.findItems());
    }

    @GetMapping("/items/{itemCode}")
    public ResponseEntity<Item> getItem(@PathVariable("itemCode") String itemCode){
        return ResponseEntity.ok(service.findItem(itemCode));
    }

    @PostMapping("/items")
    public ResponseEntity<Item> createItem(@Valid @RequestBody Item item){
        Item newItem = service.createItem(item);
        return ResponseEntity.status(HttpStatus.CREATED).body(newItem);
    }

    @PutMapping("/items")
    public ResponseEntity<Object> updateItem(@Valid @RequestBody Item item){
        service.updateItem(item);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/items/{itemCode}")
    public ResponseEntity<Object> deleteItem(@PathVariable("itemCode") String itemCode){
        service.deleteItem(itemCode);
        return ResponseEntity.ok().build();
    }
}

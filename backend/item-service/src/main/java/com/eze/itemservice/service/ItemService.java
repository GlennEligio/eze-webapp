package com.eze.itemservice.service;

import com.eze.itemservice.domain.Item;

import java.util.List;

public interface ItemService {
    List<Item> findItems();
    Item findItem(String itemCode);
    Item createItem(Item item);
    Item updateItem(Item item);
    Boolean deleteItem(String itemCode);
}

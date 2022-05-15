package com.eze.itemservice.service;

import com.eze.itemservice.domain.Item;
import com.eze.itemservice.exception.ApiException;
import com.eze.itemservice.repository.ItemRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class ItemServiceImpl implements ItemService{

    public ItemRepository repository;

    public ItemServiceImpl(ItemRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Item> findItems() {
        return repository.findByDeleteFlagFalse();
    }

    @Override
    public Item findItem(String itemCode) {
        List<Item> items = repository.findByItemCode(itemCode);
        if(items.isEmpty()){
            throw new ApiException("Item not found", HttpStatus.NOT_FOUND);
        }
        return items.get(0);
    }

    @Override
    public Item createItem(Item item) {
        List<Item> items = repository.findByItemCode(item.getItemCode());
        if(!items.isEmpty()){
            throw new ApiException("Item with same item code already exist", HttpStatus.BAD_REQUEST);
        }
        item.setDeleteFlag(false);
        return repository.save(item);
    }

    @Override
    public void updateItem(Item item) {
        List<Item> items = repository.findByItemCode(item.getItemCode());
        if(items.isEmpty()){
            throw new ApiException("Item with specified itemCode doesnt exist", HttpStatus.NOT_FOUND);
        }
        Item updatedItem = items.get(0);
        updatedItem.setDescription(item.getDescription());
        updatedItem.setCurrentAmount(item.getCurrentAmount());
        updatedItem.setTotalAmount(item.getTotalAmount());
        repository.save(updatedItem);
    }

    @Transactional
    @Override
    public void deleteItem(String itemCode) {
        List<Item> itemOp = repository.findByItemCode(itemCode);
        if(itemOp.isEmpty()){
            throw new ApiException("Item with provided itemCode doesn't 0exist", HttpStatus.NOT_FOUND);
        }
        repository.softDelete(itemCode);
    }
}

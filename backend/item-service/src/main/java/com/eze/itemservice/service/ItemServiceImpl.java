package com.eze.itemservice.service;

import com.eze.itemservice.domain.Category;
import com.eze.itemservice.domain.Item;
import com.eze.itemservice.exception.ApiException;
import com.eze.itemservice.repository.CategoryRepository;
import com.eze.itemservice.repository.ItemRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class ItemServiceImpl implements ItemService{

    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;

    public ItemServiceImpl(ItemRepository itemRepository, CategoryRepository categoryRepository) {
        this.itemRepository = itemRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Item> findItems() {
        return itemRepository.findByDeleteFlagFalse();
    }

    @Override
    public Item findItem(String itemCode) {
        Optional<Item> itemOp = itemRepository.findByItemCodeAndDeleteFlagFalse(itemCode);
        return itemOp.orElseThrow(() -> new ApiException("Item not found", HttpStatus.NOT_FOUND));
    }

    @Override
    public Item createItem(Item item) {
        Optional<Item> itemOp = itemRepository.findByItemCodeAndDeleteFlagFalse(item.getItemCode());
        if(itemOp.isPresent()){
            throw new ApiException("Item with same item code already exist", HttpStatus.BAD_REQUEST);
        }
        Optional<Category> catOp = categoryRepository.findByCategoryCode(item.getCategory().getCategoryCode());
        catOp.ifPresent(item::setCategory);
        item.setDeleteFlag(false);
        return itemRepository.save(item);
    }

    @Override
    public Item updateItem(Item item) {
        Optional<Item> itemOp = itemRepository.findByItemCodeAndDeleteFlagFalse(item.getItemCode());
        if(itemOp.isEmpty()) {
            throw new ApiException("Item with specified itemCode doesnt exist", HttpStatus.NOT_FOUND);
        }
        Item updatedItem = itemOp.get();
        updatedItem.setDescription(item.getDescription());
        updatedItem.setCurrentAmount(item.getCurrentAmount());
        updatedItem.setTotalAmount(item.getTotalAmount());

        Optional<Category> categoryOp = categoryRepository.findByCategoryCode(item.getCategory().getCategoryCode());
        categoryOp.ifPresent(updatedItem::setCategory);

        return itemRepository.save(updatedItem);
    }

    @Transactional
    @Override
    public Boolean deleteItem(String itemCode) {
        Optional<Item> itemOp = itemRepository.findByItemCodeAndDeleteFlagFalse(itemCode);
        if(itemOp.isEmpty()){
            throw new ApiException("Item with provided itemCode doesn't 0exist", HttpStatus.NOT_FOUND);
        }
        itemRepository.softDelete(itemCode);
        return true;
    }
}

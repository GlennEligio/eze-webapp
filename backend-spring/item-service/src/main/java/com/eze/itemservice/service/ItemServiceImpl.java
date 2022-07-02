package com.eze.itemservice.service;

import com.eze.itemservice.domain.Category;
import com.eze.itemservice.domain.Item;
import com.eze.itemservice.exception.ApiException;
import com.eze.itemservice.repository.CategoryRepository;
import com.eze.itemservice.repository.ItemRepository;
import org.bson.types.ObjectId;
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
    public List<Item> findItemsByCategory(String category) {
        return itemRepository.findItemByCategory(category);
    }

    @Override
    public Item findItem(String itemCode) {
        Optional<Item> itemOp = itemRepository.findByItemCodeAndDeleteFlagFalse(itemCode);
        return itemOp.orElseThrow(() -> new ApiException("Item not found", HttpStatus.NOT_FOUND));
    }

    // TODO: Update Unit Test to test the categoryCode null check
    @Override
    public Item createItem(Item item) {
        Optional<Item> itemOp = itemRepository.findByItemCodeAndDeleteFlagFalse(item.getItemCode());
        if(itemOp.isPresent()){
            throw new ApiException("Item with same item code already exist", HttpStatus.BAD_REQUEST);
        }
        item.setItemCode(new ObjectId().toHexString());
        item.setDeleteFlag(false);

        if(item.getCategory().getCategoryCode() != null) {
            Optional<Category> catOp = categoryRepository.findByCategoryCode(item.getCategory().getCategoryCode());
            catOp.ifPresent(item::setCategory);
        } else {
            Category category = item.getCategory();
            category.setCategoryCode(new ObjectId().toHexString());
            item.setCategory(categoryRepository.save(category));
        }

        return itemRepository.save(item);
    }

    // TODO: Update Unit Test to test the categoryCode null check
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

        if(item.getCategory().getCategoryCode() != null) {
            Optional<Category> catOp = categoryRepository.findByCategoryCode(item.getCategory().getCategoryCode());
            catOp.ifPresent(item::setCategory);
        } else {
            Category category = item.getCategory();
            category.setCategoryCode(new ObjectId().toHexString());
            updatedItem.setCategory(categoryRepository.save(category));
        }


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

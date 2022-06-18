package com.eze.itemservice.service;

import com.eze.itemservice.domain.Category;
import com.eze.itemservice.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService{

    private final CategoryRepository repository;

    public CategoryServiceImpl(CategoryRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Category> findCategories() {
        return repository.findAll();
    }
//
//    @Override
//    public Category findCategory(String categoryCode) {
//        return repository.findByCategoryCode(categoryCode).orElseThrow(() -> new ApiException("No Category with category code exist", HttpStatus.NOT_FOUND));
//    }
//
//    @Override
//    public Category createCategory(Category category) {
//        Optional<Category> catOp = repository.findByCategoryCode(category.getCategoryCode());
//        if(catOp.isPresent()) {
//            throw new ApiException("Category with category code already exist", HttpStatus.BAD_REQUEST);
//        }
//        return repository.save(category);
//    }
//
//    @Override
//    public Category updateCategory(Category category) {
//        Optional<Category> catOp = repository.findByCategoryCode(category.getCategoryCode());
//        Category updatedCat = catOp.orElseThrow(() -> new ApiException("Category with provided code doesnt exist", HttpStatus.NOT_FOUND));
//        updatedCat.setName(category.getName());
//        return repository.save(updatedCat);
//    }
//
//    @Override
//    public Boolean deleteCategory(String categoryCode) {
//        Optional<Category> catOp = repository.findByCategoryCode(categoryCode);
//        repository.delete(catOp.orElseThrow(() -> new ApiException("Category doesnt exist", HttpStatus.NOT_FOUND)));
//        return true;
//    }
}

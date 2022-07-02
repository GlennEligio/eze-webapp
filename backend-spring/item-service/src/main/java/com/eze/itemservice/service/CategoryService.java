package com.eze.itemservice.service;

import com.eze.itemservice.domain.Category;

import java.util.List;

public interface CategoryService {
    List<Category> findCategories();
//    Category findCategory(String categoryCode);
//    Category createCategory(Category category);
//    Category updateCategory(Category category);
//    Boolean deleteCategory(String categoryCode);
}

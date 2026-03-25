package com.flykraft.livemenu.service;

import com.flykraft.livemenu.dto.menu.CategoryRequestDto;
import com.flykraft.livemenu.entity.Category;

import java.util.List;

public interface CategoryService {
    List<Category> loadAllCategories();

    Category loadCategoryById(Long id);

    Category addNewCategory(CategoryRequestDto categoryRequestDto);

    Category updateCategory(Long categoryId, CategoryRequestDto categoryRequestDto);

    void deleteCategoryById(Long categoryId);
}

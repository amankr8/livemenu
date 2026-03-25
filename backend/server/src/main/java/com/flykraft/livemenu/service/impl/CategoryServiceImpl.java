package com.flykraft.livemenu.service.impl;

import com.flykraft.livemenu.config.TenantContext;
import com.flykraft.livemenu.dto.menu.CategoryRequestDto;
import com.flykraft.livemenu.entity.Category;
import com.flykraft.livemenu.exception.ResourceNotFoundException;
import com.flykraft.livemenu.repository.CategoryRepository;
import com.flykraft.livemenu.service.CategoryService;
import com.flykraft.livemenu.service.KitchenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final KitchenService kitchenService;

    @Override
    public List<Category> loadAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Category loadCategoryById(Long id) {
        return categoryRepository.findById(id).orElse(null);
    }

    @Override
    public Category addNewCategory(CategoryRequestDto categoryRequestDto) {
        Long currentKitchenId = TenantContext.getKitchenId();
        Category category = Category.builder()
                .kitchen(kitchenService.loadKitchenById(currentKitchenId))
                .name(categoryRequestDto.getCategoryName())
                .build();
        return categoryRepository.save(category);
    }

    @Override
    public Category updateCategory(Long categoryId, CategoryRequestDto categoryRequestDto) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category with id " + categoryId + " does not exist"));
        category.setName(categoryRequestDto.getCategoryName());
        return categoryRepository.save(category);
    }

    @Override
    public void deleteCategoryById(Long categoryId) {
        categoryRepository.deleteById(categoryId);
    }
}

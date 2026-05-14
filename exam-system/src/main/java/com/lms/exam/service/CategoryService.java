package com.lms.exam.service;

import com.lms.exam.exception.ResourceNotFoundException;
import com.lms.exam.mapper.CategoryMapper;
import com.lms.exam.model.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryMapper categoryMapper;

    public List<Category> getAllCategories() {
        return categoryMapper.findAll();
    }

    public List<Category> getAllCategoriesWithStats() {
        return categoryMapper.findAllWithStats();
    }

    public Category getCategoryById(Long categoryId) {
        return categoryMapper.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("카테고리를 찾을 수 없습니다."));
    }

    @Transactional
    public Category createCategory(Category category) {
        category.setIsActive(true);
        categoryMapper.insert(category);
        return category;
    }
    
    @Transactional
    public Category updateCategory(Category category) {
        categoryMapper.update(category);
        return categoryMapper.findById(category.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("카테고리를 찾을 수 없습니다."));
    }

    @Transactional
    public void deleteCategory(Long categoryId) {
        categoryMapper.delete(categoryId);
    }
}

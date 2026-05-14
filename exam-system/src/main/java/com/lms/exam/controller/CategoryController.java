package com.lms.exam.controller;

import com.lms.exam.dto.response.ApiResponse;
import com.lms.exam.model.Category;
import com.lms.exam.service.CategoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "카테고리")
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public ApiResponse<List<Category>> getAllCategories() {
        return ApiResponse.success(categoryService.getAllCategories());
    }

    @GetMapping("/stats")
    public ApiResponse<List<Category>> getAllCategoriesWithStats() {
        return ApiResponse.success(categoryService.getAllCategoriesWithStats());
    }
    
    @GetMapping("/{categoryId}")
    public ApiResponse<Category> getCategoryById(@PathVariable Long categoryId) {
        return ApiResponse.success(categoryService.getCategoryById(categoryId));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Category> createCategory(@RequestBody Category category) {
        return ApiResponse.success(categoryService.createCategory(category));
    }
    
    @PutMapping("/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Category> updateCategory(@PathVariable Long categoryId, @RequestBody Category category) {
        category.setCategoryId(categoryId);
        return ApiResponse.success(categoryService.updateCategory(category));
    }
    
    @DeleteMapping("/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleteCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return ApiResponse.success("카테고리가 삭제되었습니다", null);
    }
}

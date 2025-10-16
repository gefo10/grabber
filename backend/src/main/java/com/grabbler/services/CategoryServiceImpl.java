package com.grabbler.services;

import org.springframework.stereotype.Service;

import com.grabbler.models.Category;
import com.grabbler.payloads.category.CategoryDTO;
import com.grabbler.payloads.category.CategoryResponse;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Override
    public CategoryDTO getCategoryById(Long categoryId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getCategoryById'");
    }

    @Override
    public Category findCategoryById(Long categoryId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findCategoryById'");
    }

    @Override
    public CategoryDTO createCategory(Category category) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createCategory'");
    }

    @Override
    public CategoryResponse getCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getCategories'");
    }

    @Override
    public CategoryDTO updateCategory(Long categoryId, Category category) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateCategory'");
    }

    @Override
    public String deleteCategory(Long categoryId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteCategory'");
    }

}

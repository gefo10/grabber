package com.grabbler.services;

import com.grabbler.models.Category;
import com.grabbler.payloads.CategoryDTO;
import com.grabbler.payloads.CategoryResponse;

public interface CategoryService {

    CategoryDTO createCategory(CategoryDTO categoryDTO);

    CategoryResponse setCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    CategoryDTO updateCategory(Long categoryId, Category category);

    String deleteCategory(Long categoryId);

}

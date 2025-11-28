package com.grabbler.services;

import com.grabbler.models.Category;
import com.grabbler.payloads.category.*;

public interface CategoryService {

  CategoryDTO getCategoryById(Long categoryId);

  Category findCategoryById(Long categoryId);

  CategoryDTO createCategory(Category category);

  CategoryResponse getCategories(
      Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

  CategoryDTO updateCategory(Long categoryId, Category category);

  String deleteCategory(Long categoryId);
}

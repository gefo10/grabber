package com.grabbler.services;

import com.grabbler.exceptions.ResourceNotFoundException;
import com.grabbler.models.Category;
import com.grabbler.payloads.category.CategoryDTO;
import com.grabbler.payloads.category.CategoryResponse;
import com.grabbler.repositories.CategoryRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl implements CategoryService {

  @Autowired private CategoryRepository categoryRepository;

  @Autowired private ModelMapper modelMapper;

  @Override
  public CategoryDTO getCategoryById(Long categoryId) {
    Category category =
        categoryRepository
            .findById(categoryId)
            .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));
    return modelMapper.map(category, CategoryDTO.class);
  }

  @Override
  public Category findCategoryById(Long categoryId) {
    return categoryRepository
        .findById(categoryId)
        .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));
  }

  @Override
  public CategoryDTO createCategory(Category category) {
    // Check if category with same name exists
    categoryRepository
        .findByCategoryName(category.getCategoryName())
        .ifPresent(
            c -> {
              throw new RuntimeException(
                  "Category with name " + category.getCategoryName() + " already exists");
            });

    Category savedCategory = categoryRepository.save(category);
    return modelMapper.map(savedCategory, CategoryDTO.class);
  }

  @Override
  public CategoryResponse getCategories(
      Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
    Sort sort =
        sortOrder.equalsIgnoreCase("asc")
            ? Sort.by(sortBy).ascending()
            : Sort.by(sortBy).descending();

    Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
    Page<Category> categoryPage = categoryRepository.findAll(pageable);

    List<CategoryDTO> categoryDTOs =
        categoryPage.getContent().stream()
            .map(category -> modelMapper.map(category, CategoryDTO.class))
            .collect(Collectors.toList());

    CategoryResponse response = new CategoryResponse();
    response.setContent(categoryDTOs);
    response.setPageNumber(categoryPage.getNumber());
    response.setPageSize(categoryPage.getSize());
    response.setTotalElements(categoryPage.getTotalElements());
    response.setTotalPages(categoryPage.getTotalPages());
    response.setLastPage(categoryPage.isLast());

    return response;
  }

  @Override
  public CategoryDTO updateCategory(Long categoryId, Category category) {
    Category existingCategory =
        categoryRepository
            .findById(categoryId)
            .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

    existingCategory.setCategoryName(category.getCategoryName());

    Category updatedCategory = categoryRepository.save(existingCategory);
    return modelMapper.map(updatedCategory, CategoryDTO.class);
  }

  @Override
  public String deleteCategory(Long categoryId) {
    Category category =
        categoryRepository
            .findById(categoryId)
            .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

    categoryRepository.delete(category);
    return "Category deleted successfully with categoryId: " + categoryId;
  }
}

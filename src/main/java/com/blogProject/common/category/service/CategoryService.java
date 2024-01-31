package com.blogProject.common.category.service;

import com.blogProject.common.category.converter.CategoryConverter;
import com.blogProject.common.category.dto.CategoryDto;
import com.blogProject.common.category.entity.Category;
import com.blogProject.common.category.exception.CategoryException;
import com.blogProject.common.category.repository.CategoryRepository;
import com.blogProject.exception.ErrorCode;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CategoryService {

  private final CategoryRepository categoryRepository;
  private final CategoryConverter categoryConverter;

  @Transactional
  public CategoryDto createCategory(String categoryName) {
    if (categoryRepository.existsByName(categoryName)) {
      throw new CategoryException(ErrorCode.CATEGORY_ALREADY_EXISTS, categoryName);
    }

    Category category = new Category();
    category.setName(categoryName);
    return categoryConverter.entityToDto(categoryRepository.save(category));
  }

  public CategoryDto getCategory(Long id) {
    Category category = categoryRepository.findById(id)
        .orElseThrow(() -> new CategoryException(ErrorCode.CATEGORY_NOT_FOUND));
    return categoryConverter.entityToDto(category);
  }

  public List<CategoryDto> getAllCategory() {
    return categoryRepository.findAll().stream()
        .map(categoryConverter::entityToDto)
        .collect(Collectors.toList());
  }

  @Transactional
  public CategoryDto updateCategory(Long id, CategoryDto categoryDto) {
    String categoryName = categoryDto.getCategoryName();
    if (categoryRepository.existsByName(categoryName)) {
      throw new CategoryException(ErrorCode.CATEGORY_NOT_FOUND, categoryName);
    }
    Category category = categoryRepository.findById(id)
        .orElseThrow(() -> new CategoryException(ErrorCode.CATEGORY_NOT_FOUND));

    category.setName(categoryName);
    Category updatedCategory = categoryRepository.save(category);
    return categoryConverter.entityToDto(updatedCategory);
  }

  @Transactional
  public void deleteCategory(Long id) {
    Category category = categoryRepository.findById(id)
        .orElseThrow(() -> new CategoryException(ErrorCode.CATEGORY_NOT_FOUND));
    categoryRepository.delete(category);
  }
}

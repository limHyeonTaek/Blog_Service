package com.blogProject.category.service;

import com.blogProject.category.entity.Category;
import com.blogProject.category.exception.NameAlreadyExistsException;
import com.blogProject.category.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CategoryService {

  private final CategoryRepository categoryRepository;

  // 카테고리 이름 중복 검사
  @Transactional
  public Category createCategory(String categoryName) {
    if (categoryRepository.existsByName(categoryName)) {
      throw new NameAlreadyExistsException("같은 이름의 카테고리가 이미 존재합니다. " + categoryName);
    }

    // 중복이 없으먼 생성
    Category category = new Category();
    category.setName(categoryName);
    return categoryRepository.save(category);
  }

}
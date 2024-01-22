package com.blogProject.category.service;

import com.blogProject.category.entity.Category;
import com.blogProject.category.exception.CategoryNotFoundException;
import com.blogProject.category.exception.NameAlreadyExistsException;
import com.blogProject.category.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CategoryService {

  private final CategoryRepository categoryRepository;

  /**
   * 주어진 이름으로 새 카테고리를 생성하고 저장 만약 같은 이름의 카테고리가 이미 있다면 예외를 발생
   */
  @Transactional
  public Category createCategory(String categoryName) {
    if (categoryRepository.existsByName(categoryName)) {
      throw new NameAlreadyExistsException("'" + categoryName + "' 이름의 카테고리가 이미 존재합니다. ");
    }

    // 중복이 없으먼 생성
    Category category = new Category();
    category.setName(categoryName);
    return categoryRepository.save(category);
  }

  // 카테고리 조회
  public Category getCategory(Long id) {
    return categoryRepository.findById(id)
        .orElseThrow(() -> new CategoryNotFoundException("해당 카테고리가 존재하지 않습니다."));
  }

  // 모든 카테고리 조회
  public List<Category> getAllCategory() {
    return categoryRepository.findAll();
  }

}
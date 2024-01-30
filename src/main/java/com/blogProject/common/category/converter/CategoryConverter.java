package com.blogProject.common.category.converter;

import com.blogProject.common.category.dto.CategoryDto;
import com.blogProject.common.category.entity.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryConverter {

  // Category 엔티티를 CategoryDto로 변환하는 메서드
  public CategoryDto entityToDto(Category category) {
    return CategoryDto.builder()
        .categoryName(category.getName())
        .build();
  }

  // CategoryDto를 Category 엔티티로 변환하는 메서드
  public Category dtoToEntity(CategoryDto categoryDto) {
    Category category = new Category();
    category.setName(categoryDto.getCategoryName());
    return category;
  }
}

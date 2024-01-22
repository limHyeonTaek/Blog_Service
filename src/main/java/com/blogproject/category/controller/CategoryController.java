package com.blogProject.category.controller;

import com.blogProject.category.dto.model.CategoryDto;
import com.blogProject.category.entity.Category;
import com.blogProject.category.exception.NameAlreadyExistsException;
import com.blogProject.category.service.CategoryService;
import javax.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/category")
public class CategoryController {

  private final CategoryService categoryService;

  // 카테고리 생성
  @PostMapping
  public ResponseEntity<?> createCategory(@Valid @RequestBody CategoryDto categoryDto) {
    try {
      Category category = categoryService.createCategory(categoryDto.getCategoryName());
      return new ResponseEntity<>(category, HttpStatus.OK);
    } catch (NameAlreadyExistsException e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.CREATED);
    }
  }

  // 카테고리 전체 보기
  @GetMapping
  public ResponseEntity<?> allCategory() {
    List<Category> category = categoryService.getAllCategory();
    return new ResponseEntity<>(category, HttpStatus.OK);
  }

  // 카테고리 수정
  @PatchMapping("/{id}")
  public ResponseEntity<?> updateCategory(@PathVariable Long id,
      @Valid @RequestBody CategoryDto categoryName) {
    Category category = categoryService.updateCategory(id, categoryName);
    return new ResponseEntity<>(category, HttpStatus.OK);
  }

  // 카테고리 삭제
  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
    categoryService.deleteCategory(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

}
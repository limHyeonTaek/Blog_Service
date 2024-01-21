package com.blogProject.category.controller;

import com.blogProject.category.dto.model.CategoryDto;
import com.blogProject.category.entity.Category;
import com.blogProject.category.exception.NameAlreadyExistsException;
import com.blogProject.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/category")
public class CategoryController {

  private final CategoryService categoryService;

  @PostMapping
  public ResponseEntity<?> createCategory(@RequestBody CategoryDto categoryDto) {
    try {
      Category category = categoryService.createCategory(categoryDto.getCategoryName());
      return new ResponseEntity<>(category, HttpStatus.OK);
    } catch (NameAlreadyExistsException e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
    }
  }
}
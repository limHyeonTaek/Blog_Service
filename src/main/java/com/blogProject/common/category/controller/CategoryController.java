package com.blogProject.common.category.controller;

import com.blogProject.common.category.dto.CategoryDto;
import com.blogProject.common.category.service.CategoryService;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/category")
public class CategoryController {

  private final CategoryService categoryService;

  // 카테고리 생성 API
  @PostMapping
  public ResponseEntity<CategoryDto> createCategory(@Valid @RequestBody CategoryDto categoryDto) {
    CategoryDto responseDto = categoryService.createCategory(categoryDto.getCategoryName());
    return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
  }

  // 카테고리 조회 API
  @GetMapping
  public ResponseEntity<List<CategoryDto>> allCategory() {
    List<CategoryDto> categoryDtos = categoryService.getAllCategory();
    return ResponseEntity.ok(categoryDtos);
  }

  // 카테고리 수정 API
  @PutMapping("/{id}")
  public ResponseEntity<CategoryDto> updateCategory(@PathVariable Long id,
      @Valid @RequestBody CategoryDto categoryDto) {
    CategoryDto responseDto = categoryService.updateCategory(id, categoryDto);
    return ResponseEntity.ok(responseDto);
  }

  // 카테고리 삭제 API
  @DeleteMapping("/{id}")
  public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
    categoryService.deleteCategory(id);
    return ResponseEntity.ok("성공적으로 삭제 되었습니다.");
  }
}


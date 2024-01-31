package com.blogProject.common.category.controller;

import com.blogProject.common.category.dto.CategoryDto;
import com.blogProject.common.category.exception.CategoryException;
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

  // 카테고리 생성
  @PostMapping
  public ResponseEntity<?> createCategory(@Valid @RequestBody CategoryDto categoryDto) {
    try {
      CategoryDto responseDto = categoryService.createCategory(categoryDto.getCategoryName());
      return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    } catch (CategoryException e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
  }

  // 카테고리 전체 보기
  @GetMapping
  public ResponseEntity<?> allCategory() {
    List<CategoryDto> categoryDtos = categoryService.getAllCategory();
    return new ResponseEntity<>(categoryDtos, HttpStatus.OK);
  }

  // 카테고리 수정
  @PutMapping("/{id}")
  public ResponseEntity<?> updateCategory(@PathVariable Long id,
      @Valid @RequestBody CategoryDto categoryDto) {
    CategoryDto responseDto = categoryService.updateCategory(id, categoryDto);
    return new ResponseEntity<>(responseDto, HttpStatus.OK);
  }

  // 카테고리 삭제
  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
    categoryService.deleteCategory(id);
    return new ResponseEntity<>("성공적으로 삭제 되었습니다.", HttpStatus.OK);
  }

}


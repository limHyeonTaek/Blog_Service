package com.blogProject.category.dto.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {

  @NotBlank(message = "카테고리 이름은 필수입니다.")
  private String categoryName;

}
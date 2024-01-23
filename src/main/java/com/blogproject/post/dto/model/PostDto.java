package com.blogProject.post.dto.model;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {

  @NotNull(message = "제목을 입력해 주세요.")
  private String title;

  @NotNull(message = "내용을 입력해 주세요.")
  private String contents;

  private String categoryName;

}

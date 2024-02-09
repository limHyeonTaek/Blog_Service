package com.blogProject.common.post.dto.model;

import java.time.LocalDateTime;
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

  private Long PostId;

  @NotNull(message = "제목을 입력해 주세요.")
  private String title;

  @NotNull(message = "내용을 입력해 주세요.")
  private String contents;

  private String categoryName;

  private String memberName;

  private String imageUrl;

  private LocalDateTime createdDate;

  private LocalDateTime updatedDate;

}

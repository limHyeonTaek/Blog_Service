package com.blogProject.common.comment.dto.model;

import com.blogProject.common.comment.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReplyDto {

  private Long commentId;
  private String comments;
  private String member;
  private Long postId;
  private Long parentId;

  public static ReplyDto fromEntity(Comment comment) {
    ReplyDto replyDto = ReplyDto.builder()
        .commentId(comment.getId())
        .comments(comment.getComments())
        .member(comment.getMember().getEmail())
        .postId(comment.getPost().getId())
        .build();

    if (comment.getParentComment() != null) {
      replyDto.setParentId(comment.getParentComment().getId());
    }

    return replyDto;
  }
}

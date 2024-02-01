package com.blogProject.common.comment.dto.model;

import com.blogProject.common.comment.entity.Comment;
import com.blogProject.common.member.entity.Member;
import com.blogProject.common.post.entity.Post;
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
public class CommentDto {

  private Long commentId;
  private String comments;
  private Member member;
  private Post post;

  public static CommentDto fromEntity(Comment comment) {
    return CommentDto.builder()
        .commentId(comment.getId())
        .comments(comment.getComments())
        .member(comment.getMember())
        .post(comment.getPost())
        .build();
  }

  public static Comment fromDto(CommentDto commentDto) {
    return Comment.builder()
        .comments(commentDto.getComments())
        .member(commentDto.getMember())
        .post(commentDto.getPost())
        .build();
  }
}

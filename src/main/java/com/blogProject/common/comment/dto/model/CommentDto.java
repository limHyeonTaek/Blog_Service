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
  private String member;
  private Long postId;

  public static CommentDto fromEntity(Comment comment) {
    return CommentDto.builder()
        .commentId(comment.getId())
        .comments(comment.getComments())
        .member(comment.getMember().getEmail())
        .postId(comment.getPost().getId())
        .build();
  }

  public Comment dtoToEntity(CommentDto commentDto, Member member, Post post) {
    return Comment.builder()
        .id(commentDto.getCommentId())
        .comments(commentDto.getComments())
        .member(member)
        .post(post)
        .build();
  }
}

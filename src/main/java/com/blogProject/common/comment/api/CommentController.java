package com.blogProject.common.comment.api;

import com.blogProject.common.comment.dto.WriteReply;
import com.blogProject.common.comment.dto.model.CommentDto;
import com.blogProject.common.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

  private final CommentService commentService;

  @PostMapping("/{postId}")
  public ResponseEntity<?> writeComment(@PathVariable Long postId, @RequestBody String request,
      Authentication authentication) {
    commentService.writeComment(postId, request, authentication);
    return ResponseEntity.ok("댓글 작성이 완료되었습니다.");
  }

  @PutMapping("/{commentId}")
  public ResponseEntity<?> updateComment(@PathVariable Long commentId, @RequestBody String request,
      Authentication authentication) {
    commentService.updateComment(commentId, request, authentication);
    return ResponseEntity.ok("수정이 완료 되었습니다.");
  }

  @DeleteMapping("/{commentId}")
  public ResponseEntity<?> deleteComment(@PathVariable Long commentId,
      Authentication authentication) {
    commentService.deleteComment(commentId, authentication);
    return ResponseEntity.ok("댓글 삭제가 완료되었습니다.");
  }

  @GetMapping("/{postId}")
  public ResponseEntity<?> getComments(@PathVariable Long postId,
      Pageable pageable) {
    Page<CommentDto> commentDtos = commentService.getComments(postId, pageable);
    return ResponseEntity.ok(commentDtos);
  }

  @PostMapping("/{commentId}/reply")
  public ResponseEntity<?> writeReply(@PathVariable Long commentId, @RequestBody WriteReply request,
      Authentication authentication) {
    commentService.writeReply(commentId, request, authentication);
    return ResponseEntity.ok("대댓글 작성이 완료되었습니다.");
  }

}

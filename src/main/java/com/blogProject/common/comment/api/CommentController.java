package com.blogProject.common.comment.api;

import com.blogProject.common.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
}

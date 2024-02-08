package com.blogProject.exception;

import com.blogProject.common.category.exception.CategoryException;
import com.blogProject.common.comment.exception.CommentException;
import com.blogProject.common.member.exception.MemberException;
import com.blogProject.common.post.exception.PostException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(CategoryException.class)
  public ResponseEntity<?> handleCategoryException(CategoryException e) {
    return toResponse(e.getErrorCode(), e.getMessage());
  }

  @ExceptionHandler(MemberException.class)
  public ResponseEntity<?> handleMemberException(MemberException e) {
    return toResponse(e.getErrorCode(), e.getMessage());
  }

  @ExceptionHandler(PostException.class)
  public ResponseEntity<?> handlePostException(PostException e) {
    return toResponse(e.getErrorCode(), e.getMessage());
  }

  @ExceptionHandler(CommentException.class)
  public ResponseEntity<?> handleCommentException(CommentException e) {
    return toResponse(e.getErrorCode(), e.getMessage());
  }

  @ExceptionHandler(GlobalException.class)
  public ResponseEntity<?> handleGlobalException(GlobalException e) {
    return toResponse(e.getErrorCode(), e.getMessage());
  }


  private static ResponseEntity<ErrorResponse> toResponse(
      ErrorCode errorCode, String message) {
    return ResponseEntity.status(errorCode.getStatus())
        .body(new ErrorResponse(errorCode, message));
  }

}


package com.blogProject.exception;

import com.blogProject.common.category.exception.CategoryException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(CategoryException.class)
  public ResponseEntity<?> handleCategoryException(CategoryException e) {
    return toResponse(e.getErrorCode(), e.getMessage());
  }

  private static ResponseEntity<ErrorResponse> toResponse(
      ErrorCode errorCode, String message) {
    return ResponseEntity.status(errorCode.getStatus())
        .body(new ErrorResponse(errorCode, message));
  }

  // 다른 예외 처리 메서드들...
}


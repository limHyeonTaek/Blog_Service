package com.blogProject.common.comment.exception;

import com.blogProject.exception.CustomException;
import com.blogProject.exception.ErrorCode;

public class CommentException extends CustomException {

  public CommentException(ErrorCode errorCode) {
    super(errorCode);
  }

  public CommentException(ErrorCode errorCode, String msg) {
    super(errorCode, msg);
  }
}

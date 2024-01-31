package com.blogProject.common.post.exception;

import com.blogProject.exception.CustomException;
import com.blogProject.exception.ErrorCode;

public class PostException extends CustomException {

  public PostException(ErrorCode errorCode) {
    super(errorCode);
  }

  public PostException(ErrorCode errorCode, String msg) {
    super(errorCode, msg);
  }
}

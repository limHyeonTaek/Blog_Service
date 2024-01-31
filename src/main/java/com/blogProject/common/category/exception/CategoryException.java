package com.blogProject.common.category.exception;

import com.blogProject.exception.CustomException;
import com.blogProject.exception.ErrorCode;

public class CategoryException extends CustomException {

  public CategoryException(ErrorCode errorCode) {
    super(errorCode);
  }

  public CategoryException(ErrorCode errorCode, String msg) {
    super(errorCode, msg);
  }
}

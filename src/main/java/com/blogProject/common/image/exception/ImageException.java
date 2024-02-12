package com.blogProject.common.image.exception;

import com.blogProject.exception.CustomException;
import com.blogProject.exception.ErrorCode;

public class ImageException extends CustomException {

  public ImageException(ErrorCode errorCode) {
    super(errorCode);
  }

  public ImageException(ErrorCode errorCode, String msg) {
    super(errorCode, msg);
  }
}


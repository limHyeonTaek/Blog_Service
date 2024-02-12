package com.blogProject.common.image.exception;

import com.blogProject.exception.CustomException;
import com.blogProject.exception.ErrorCode;

public class S3Exception extends CustomException {

  public S3Exception(ErrorCode errorCode) {
    super(errorCode);
  }

  public S3Exception(ErrorCode errorCode, String msg) {
    super(errorCode, msg);
  }
}

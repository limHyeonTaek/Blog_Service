package com.blogProject.exception;


public class GlobalException extends CustomException {

  public GlobalException(ErrorCode errorCode) {
    super(errorCode);
  }

  public GlobalException(ErrorCode errorCode, String msg) {
    super(errorCode, msg);
  }
}

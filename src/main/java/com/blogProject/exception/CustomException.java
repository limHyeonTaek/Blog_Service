package com.blogProject.exception;

import lombok.Getter;

/**
 * 커스텀 예외를 처리하기 위한 추상 클래스입니다. 예외처리를 위해 필요한 제약을 하위 클래스에 넘겨주기 위해 생성자를 정의합니다.
 */
@Getter
public abstract class CustomException extends RuntimeException {

  private final ErrorCode errorCode;
  private final String message;

  public CustomException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
    this.message = errorCode.getMessage();
  }

  public CustomException(ErrorCode errorCode, String msg) {
    super(msg);
    this.errorCode = errorCode;
    this.message = msg;
  }
}

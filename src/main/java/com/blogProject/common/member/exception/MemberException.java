package com.blogProject.common.member.exception;


import com.blogProject.exception.CustomException;
import com.blogProject.exception.ErrorCode;

public class MemberException extends CustomException {

  public MemberException(ErrorCode errorCode) {
    super(errorCode);
  }

  public MemberException(ErrorCode errorCode, String msg) {
    super(errorCode, msg);
  }
}

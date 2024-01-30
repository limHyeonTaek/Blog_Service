package com.blogProject.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
  // member
  MEMBER_NOT_FOUND(NOT_FOUND, "회원을 찾을 수 없습니다."),
  MEMBER_ALREADY_EXISTS(BAD_REQUEST, "이미 존재하는 회원입니다."),
  INVALID_REQUEST(BAD_REQUEST, "잘못된 요청입니다.");


  private final HttpStatus status;
  private final String message;
}

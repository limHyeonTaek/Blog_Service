package com.blogProject.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
  // auth
  INVALID_LOGIN_REQUEST(BAD_REQUEST, "잘못된 로그인 요청 입니다."),

  // member
  MEMBER_NOT_FOUND(NOT_FOUND, "회원을 찾을 수 없습니다."),
  MEMBER_ALREADY_EXISTS(BAD_REQUEST, "이미 존재하는 회원입니다."),
  INVALID_REQUEST(BAD_REQUEST, "잘못된 요청입니다."),
  MEMBER_WITHDRAWAL(BAD_REQUEST, "탈퇴한 회원입니다."),

  // category
  CATEGORY_NOT_FOUND(NOT_FOUND, "카테고리를 찾을 수 없습니다."),
  CATEGORY_ALREADY_EXISTS(BAD_REQUEST, "이미 존재하는 카테고리 입니다."),

  // post
  POST_NOT_FOUND(NOT_FOUND, "게시글을 찾을 수 없습니다."),

  //comment
  COMMENT_NOT_FOUND(NOT_FOUND, "댓글을 찾을 수 없습니다."),

  // global
  ACCESS_DENIED(FORBIDDEN, "권한이 없습니다."),
  ACCESS_DENIED_EXCEPTION(FORBIDDEN, "금지된 접근입니다."),

  // S3
  S3_FILE_CONVERT_ERROR(BAD_REQUEST, "파일 변환이 실패하였습니다."),
  S3_FILE_DELETE_ERROR(BAD_REQUEST, "파일 삭제가 실패하였습니다."),

  //image
  IMAGE_NOT_FOUND(NOT_FOUND, "이미지를 찾을 수 없습니다."),
  ;

  private final HttpStatus status;
  private final String message;
}

package com.blogProject.exception;

public record ErrorResponse(
    ErrorCode errorCode,
    String message
) {

}

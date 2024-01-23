package com.blogProject.category.exception;

public class NameAlreadyExistsException extends RuntimeException {

  public NameAlreadyExistsException(String message) {
    super(message);
  }
}

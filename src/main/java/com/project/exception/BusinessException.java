package com.project.exception;

public class BusinessException extends RuntimeException {
  public BusinessException(String m) {
    super(m);
  }

  public BusinessException(String m, Throwable c) {
    super(m, c);
  }
}

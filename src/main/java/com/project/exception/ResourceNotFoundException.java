package com.project.exception;

public class ResourceNotFoundException extends RuntimeException {
  public ResourceNotFoundException(String m) {
    super(m);
  }
}

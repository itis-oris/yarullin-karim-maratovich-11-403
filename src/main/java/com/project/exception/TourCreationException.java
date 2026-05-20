package com.project.exception;

public class TourCreationException extends BusinessException {
  public TourCreationException(String m) {
    super(m);
  }

  public TourCreationException(String m, Throwable c) {
    super(m, c);
  }
}

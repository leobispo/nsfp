package com.charite.exception;

public class NSFPManagerException extends RuntimeException {
  private static final long serialVersionUID = 1717065277942954392L;

  public NSFPManagerException() {
    super();
  }

  public NSFPManagerException(String message) {
    super(message);
  }

  public NSFPManagerException(String message, Exception e) {
    super(message, e);
  }

  public NSFPManagerException(String message, Throwable e) {
    super(message, e);
  }
}

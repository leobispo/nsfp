package com.charite.exception;

public class InvalidFormatException extends RuntimeException {
  private static final long serialVersionUID = -8124646766304138462L;

  public InvalidFormatException() {
  }

  public InvalidFormatException(String message) {
    super(message);
  }
}

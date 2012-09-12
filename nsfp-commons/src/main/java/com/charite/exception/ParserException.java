package com.charite.exception;

public class ParserException extends RuntimeException {
  private static final long serialVersionUID = 5376524864807269024L;

  public ParserException() {
    super();
  }

  public ParserException(String message) {
    super(message);
  }
}

package com.charite.exception;

public class ConverterException extends RuntimeException {

  private static final long serialVersionUID = -4521896760337266317L;

  public ConverterException() {
    super();
  }

  public ConverterException(String message) {
    super(message);
  }
  
  public ConverterException(String message, Exception e) {
    super(message, e);
  }
}

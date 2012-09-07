package com.charite.exception;

public class DownloadException extends RuntimeException {
  private static final long serialVersionUID = 5376524864807269024L;
  
  public DownloadException() {
    super();
  }

  public DownloadException(String message) {
    super(message);
  }
  
  public DownloadException(String message, Exception e) {
    super(message, e);
  }
}

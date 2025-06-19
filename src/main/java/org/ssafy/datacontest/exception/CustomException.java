package org.ssafy.datacontest.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.ssafy.datacontest.enums.ErrorCode;

@Getter
public class CustomException extends RuntimeException {
  private final HttpStatus status;
  private final String code;
  private final String message;

  public CustomException(HttpStatus status, String code, String message) {
    this.status = status;
    this.code = code;
    this.message = message;
  }

  public CustomException(HttpStatus httpStatus, ErrorCode errorCode) {
    this.status = httpStatus;
    this.code = errorCode.getCode();
    this.message = errorCode.getMessage();
  }
}
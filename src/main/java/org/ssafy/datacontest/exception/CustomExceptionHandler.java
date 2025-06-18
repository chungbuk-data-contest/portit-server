package org.ssafy.datacontest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.ssafy.datacontest.dto.error.ErrorDto;

@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(CustomException.class)
    private ResponseEntity<ErrorDto> handleCustomException(CustomException e) {
        return ErrorDto.toResponseEntity(e);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDto> handleValidationExceptions(MethodArgumentNotValidException ex) {
        StringBuilder messageBuilder = new StringBuilder();

        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            messageBuilder
                    .append("[")
                    .append(fieldError.getField())
                    .append("] ")
                    .append(fieldError.getDefaultMessage())
                    .append("; ");
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorDto.builder()
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .code("VALIDATION_ERROR")
                        .message(messageBuilder.toString().trim())
                        .build());
    }
}


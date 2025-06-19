package org.ssafy.datacontest.dto.error;


import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.ssafy.datacontest.exception.CustomException;

@Getter
@Builder
public class ErrorDto {

    private HttpStatus httpStatus;
    private String message;
    private String code;

    public static ResponseEntity<ErrorDto> toResponseEntity(CustomException ex) {
        return ResponseEntity
                .status(ex.getStatus())
                .body(ErrorDto.builder()
                        .httpStatus(ex.getStatus())
                        .code(ex.getCode())
                        .message(ex.getMessage())
                        .build());
    }
}

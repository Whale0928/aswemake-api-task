package com.aswemake.api.aswemakeapitask.exception;

import com.aswemake.api.aswemakeapitask.dto.GlobalResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.Objects;

import static java.time.LocalDateTime.now;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final LocalDateTime timestamp = now();

    //Valid Exception 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GlobalResponse> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessage = Objects.requireNonNull(ex.getBindingResult().getFieldError()).getDefaultMessage();
        return new ResponseEntity<>(GlobalResponse.builder()
                .status(HttpStatus.BAD_REQUEST)
                .timestamp(timestamp)
                .message(errorMessage)
                .build(), HttpStatus.BAD_REQUEST);
    }
    // MethodArgumentTypeMismatchException 처리
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<GlobalResponse> handleTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        String errorMessage = "잘못된 요청 파라미터 타입입니다: " + ex.getName() + " should be of type " + ex.getRequiredType().getName();
        return new ResponseEntity<>(GlobalResponse.builder()
                .status(HttpStatus.BAD_REQUEST)
                .timestamp(timestamp)
                .message(errorMessage)
                .build(), HttpStatus.BAD_REQUEST);
    }

    // CustomException 처리
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<GlobalResponse> handleCustomException(CustomException ex) {
        return new ResponseEntity<>(GlobalResponse.builder()
                .status(ex.getStatus())
                .timestamp(timestamp)
                .message(ex.getMessage())
                .build(), ex.getStatus());
    }
}

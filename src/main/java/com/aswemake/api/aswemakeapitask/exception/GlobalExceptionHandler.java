package com.aswemake.api.aswemakeapitask.exception;

import com.aswemake.api.aswemakeapitask.dto.GlobalResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

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
}

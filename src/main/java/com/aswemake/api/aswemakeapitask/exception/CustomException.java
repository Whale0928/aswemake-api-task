package com.aswemake.api.aswemakeapitask.exception;


import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomException extends RuntimeException {
    private final HttpStatus status;
    private final ErrorMessages errorMessages;

    public CustomException(HttpStatus status, ErrorMessages errorMessages) {
        super(errorMessages.getMessage());
        this.status = status;
        this.errorMessages = errorMessages;
    }
}

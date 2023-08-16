package com.aswemake.api.aswemakeapitask.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@Builder
public class GlobalResponse {
    HttpStatus status;
    LocalDateTime timestamp;
    String message;
    Object data;
}

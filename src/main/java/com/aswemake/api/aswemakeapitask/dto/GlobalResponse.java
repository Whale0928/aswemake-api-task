package com.aswemake.api.aswemakeapitask.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

@Getter
@Builder
public class GlobalResponse {
    HttpStatus status;
    LocalDateTime timestamp;
    String message;
    Object data;

    public static ResponseEntity<Object> ok(Object data) {
        return buildResponse(HttpStatus.OK, "성공", data);
    }

    public static ResponseEntity<Object> ok(String message, Object data) {
        return buildResponse(HttpStatus.OK, message, data);
    }

    public static ResponseEntity<Object> fail(Object data) {
        return buildResponse(HttpStatus.BAD_REQUEST, "실패", data);
    }

    public static ResponseEntity<Object> fail(String message, Object data) {
        return buildResponse(HttpStatus.BAD_REQUEST, message, data);
    }

    private static ResponseEntity<Object> buildResponse(HttpStatus status, String message, Object data) {
        return ResponseEntity.status(status).body(GlobalResponse.builder()
                .status(status)
                .timestamp(LocalDateTime.now())
                .message(message)
                .data(data)
                .build());
    }
}

package com.aswemake.api.aswemakeapitask.dto;

import com.aswemake.api.aswemakeapitask.dto.item.response.ItemCreateResponseDto;
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

    public static ResponseEntity<GlobalResponse> ok(Object data) {
        return buildResponse(HttpStatus.OK, "성공", data);
    }

    public static ResponseEntity<GlobalResponse> ok(String message, Object data) {
        return buildResponse(HttpStatus.OK, message, data);
    }

    public static ResponseEntity<GlobalResponse> fail(Object data) {
        return buildResponse(HttpStatus.BAD_REQUEST, "실패", data);
    }

    public static ResponseEntity<GlobalResponse> fail(String message, Object data) {
        return buildResponse(HttpStatus.BAD_REQUEST, message, data);
    }

    private static ResponseEntity<GlobalResponse> buildResponse(HttpStatus status, String message, Object data) {
        return ResponseEntity.status(status).body(GlobalResponse.builder()
                .status(status)
                .timestamp(LocalDateTime.now())
                .message(message)
                .data(data)
                .build());
    }

    public static ResponseEntity<GlobalResponse> created(ItemCreateResponseDto item) {
        return ResponseEntity.status(HttpStatus.CREATED).body(GlobalResponse.builder()
                .status(HttpStatus.CREATED)
                .timestamp(LocalDateTime.now())
                .message("CREATED")
                .data(item)
                .build());
    }
    public static ResponseEntity<GlobalResponse> created(String message,ItemCreateResponseDto item) {
        return ResponseEntity.status(HttpStatus.CREATED).body(GlobalResponse.builder()
                .status(HttpStatus.CREATED)
                .timestamp(LocalDateTime.now())
                .message(message)
                .data(item)
                .build());
    }
}

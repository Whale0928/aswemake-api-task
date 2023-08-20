package com.aswemake.api.aswemakeapitask.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CodeGeneratorTest {

    @Test
    @DisplayName("주문 코드 생성 테스트")
    void testCreateOrderCode() {
        // Given
        Long userId = 12345L;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMM-ddHHmmss");
        String currentDate = LocalDateTime.now().format(formatter);
        String expectedOrderCode = currentDate + "_" + userId;

        // When
        String actualOrderCode = CodeGenerator.createOrderCode(userId);

        // Then
        assertEquals(expectedOrderCode, actualOrderCode);
    }
}
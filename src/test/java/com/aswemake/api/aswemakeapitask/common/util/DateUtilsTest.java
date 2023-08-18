package com.aswemake.api.aswemakeapitask.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DateUtilsTest {

    @Test
    @DisplayName("유효한 날짜 형식은 True가 반환된다")
    void testIsValidDateFormat_validDate() {
        String validDate = "20230101-12:00:00";
        assertTrue(DateUtils.isValidDateFormat(validDate));
    }

    @Test
    @DisplayName("유효하지 않은 시간(25시)은 False가 반환된다")
    void testIsValidDateFormat_invalidHour() {
        String invalidHour = "20230101-25:00:00";
        assertFalse(DateUtils.isValidDateFormat(invalidHour));
    }

    @Test
    @DisplayName("유효하지 않은 월(13월)은 False가 반환된다")
    void testIsValidDateFormat_invalidMonth() {
        String invalidMonth = "20231301-12:00:00";
        assertFalse(DateUtils.isValidDateFormat(invalidMonth));
    }

    @Test
    @DisplayName("유효하지 않은 일(32일)은 False가 반환된다")
    void testIsValidDateFormat_invalidDay() {
        String invalidDay = "20230132-12:00:00";
        assertFalse(DateUtils.isValidDateFormat(invalidDay));
    }

    @Test
    @DisplayName("유효하지 않은 분(60분)은 False가 반환된다")
    void testIsValidDateFormat_invalidMinute() {
        String invalidMinute = "20230101-12:60:00";
        assertFalse(DateUtils.isValidDateFormat(invalidMinute));
    }

    @Test
    @DisplayName("유효하지 않은 초(60초)은 False가 반환된다")
    void testIsValidDateFormat_invalidSecond() {
        String invalidSecond = "20230101-12:00:60";
        assertFalse(DateUtils.isValidDateFormat(invalidSecond));
    }

    @Test
    @DisplayName("유효하지 않은 날짜 형식은 False가 반환된다")
    void testIsValidDateFormat_invalidFormat() {
        String invalidFormat = "01-01-2023 12:00:00";
        assertFalse(DateUtils.isValidDateFormat(invalidFormat));
    }

    @Test
    @DisplayName("유효하지 않은 구분자 사용은 False가 반환된다")
    void testIsValidDateFormat_invalidDelimiter() {
        String invalidDelimiter = "20230101_12:00:00";
        assertFalse(DateUtils.isValidDateFormat(invalidDelimiter));
    }
}
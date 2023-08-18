package com.aswemake.api.aswemakeapitask.common.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateUtils {
    //ISO 8601

    public static boolean isValidDateFormat(String date) {
        try {
            LocalDateTime.parse(date);  // ISO 8601 형식을 기본으로 지원합니다.
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public static LocalDateTime parseDate(String date) throws DateTimeParseException {
        return LocalDateTime.parse(date);
    }
}

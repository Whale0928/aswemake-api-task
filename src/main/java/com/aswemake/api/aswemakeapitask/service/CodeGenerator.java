package com.aswemake.api.aswemakeapitask.service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CodeGenerator {

    public static String createOrderCode(Long userId) {
        // 주문일자(년월일시분초) 포맷팅
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMM-ddHHmmss");
        String currentDate = LocalDateTime.now().format(formatter);
        // 주문일자 + _ + 주문자 아이디로 주문 코드 생성
        return currentDate + "_" + userId;
    }

}

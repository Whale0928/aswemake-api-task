package com.aswemake.api.aswemakeapitask.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorMessages {

    //Auth
    PASSWORD_NOT_MATCH("비밀번호가 일치하지 않습니다."),
    USER_NOT_FOUND("사용자를 찾을 수 없습니다."),

    //Coupon
    COUPON_NOT_FOUND("쿠폰을 찾을 수 없습니다.");


    private final String message;
}

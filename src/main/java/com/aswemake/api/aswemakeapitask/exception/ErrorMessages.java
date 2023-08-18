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
    COUPON_NOT_FOUND("쿠폰을 찾을 수 없습니다."),

    //Item
    ITEM_NOT_FOUND("상품을 찾을 수 없습니다."),
    ITEM_STOCK_NOT_ENOUGH("상품의 재고가 부족합니다."),
    ITEM_NAME_DUPLICATION("동일한 이름이 존재합니다."),
    ITEM_PRICE_NOT_ENOUGH("상품의 가격이 부족합니다.");

    private final String message;
}

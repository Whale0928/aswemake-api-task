package com.aswemake.api.aswemakeapitask.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorMessages {

    //COMMON
    INVALID_INPUT_VALUE("잘못된 입력값입니다."),
    INVALID_DATE_VALUE("잘못된 날짜값입니다."),

    //Auth
    PASSWORD_NOT_MATCH("비밀번호가 일치하지 않습니다."),
    USER_NOT_FOUND("사용자를 찾을 수 없습니다."),

    //Coupon
    COUPON_NOT_FOUND("쿠폰을 찾을 수 없습니다."),
    COUPON_ALREADY_USED("이미 사용된 쿠폰입니다."),
    COUPON_NOT_AVAILABLE("사용할 수 없는 쿠폰입니다."),
    COUPON_DISCOUNTED_AMOUNT_EXCEEDS_ORDER_VALUE("쿠폰 할인 금액이 주문 금액을 초과합니다."),
    COUPON_DISCOUNT_CANNOT_EXCEED_100("할인은 100%를 초과할 수 없습니다."),

    //Item
    ITEM_NOT_FOUND("상품을 찾을 수 없습니다."),
    ITEM_STOCK_NOT_ENOUGH("상품의 재고가 부족합니다."),
    ITEM_NAME_DUPLICATION("동일한 이름이 존재합니다."),
    PRICE_MUSE_BE_NOT_NULL("가격은 null이 될 수 없습니다."),
    ITEM_PRICE_NOT_CHANGED("가격이 기존과 동일합니다."),
    ITEM_DELETE_NOT_POSSIBLE("주문내역이 존재하는 상품은 삭제할 수 없습니다."),
    ITEM_PRICE_NOT_ENOUGH("상품의 가격이 부족합니다.");

    private final String message;
}

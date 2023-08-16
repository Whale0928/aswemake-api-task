package com.aswemake.api.aswemakeapitask.domain.coupon;

import lombok.Getter;

@Getter
public enum CouponScope {
    ALL_ORDER, // 주문 전체
    SPECIFIC_ITEM // 특정 상품 한정
}

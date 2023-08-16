package com.aswemake.api.aswemakeapitask.domain.coupon;


import lombok.Getter;

@Getter
public enum CouponType {
    PERCENTAGE, // 비율 할인
    FIXED // 고정 금액 할인
}

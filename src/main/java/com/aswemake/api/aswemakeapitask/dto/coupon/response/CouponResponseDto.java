package com.aswemake.api.aswemakeapitask.dto.coupon.response;


import com.aswemake.api.aswemakeapitask.domain.coupon.CouponScope;
import com.aswemake.api.aswemakeapitask.domain.coupon.CouponType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CouponResponseDto {
    private Long id;
    private String name;
    private CouponType couponType;
    private CouponScope couponScope;
    private Double discountValue;
    private Long orderId; // 사용된 주문
    private Long itemId;  // 특정 상품 한정 쿠폰의 경우, 해당 상품 정보
    private String itemName;  // 특정 상품 한정 쿠폰의 경우, 해당 상품 정보
}

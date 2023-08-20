package com.aswemake.api.aswemakeapitask.service;

import com.aswemake.api.aswemakeapitask.domain.coupon.Coupon;
import com.aswemake.api.aswemakeapitask.domain.coupon.CouponRepository;
import com.aswemake.api.aswemakeapitask.dto.coupon.response.CouponResponseDto;
import com.aswemake.api.aswemakeapitask.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import static com.aswemake.api.aswemakeapitask.exception.ErrorMessages.COUPON_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class CouponService {
    private final CouponRepository couponRepository;
    public CouponResponseDto selectCoupon(Long id) {
        Coupon coupon = couponRepository.findByIdWithItemAndOrders(id)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, COUPON_NOT_FOUND));
        return CouponResponseDto.builder()
                .id(coupon.getId())
                .name(coupon.getName())
                .couponType(coupon.getCouponType())
                .discountValue(coupon.getDiscountValue())
                .couponScope(coupon.getCouponScope())
                .orderId(coupon.getOrders() == null ? null : coupon.getOrders().getId())
                .itemId(coupon.getItem() == null ? null : coupon.getItem().getId())
                .itemName(coupon.getItem() == null ? null : coupon.getItem().getName())
                .build();
    }

}

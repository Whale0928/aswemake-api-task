package com.aswemake.api.aswemakeapitask.service;

import com.aswemake.api.aswemakeapitask.domain.coupon.Coupon;
import com.aswemake.api.aswemakeapitask.domain.coupon.CouponRepository;
import com.aswemake.api.aswemakeapitask.domain.coupon.CouponScope;
import com.aswemake.api.aswemakeapitask.domain.coupon.CouponType;
import com.aswemake.api.aswemakeapitask.dto.orders.request.OrderItemRequest;
import com.aswemake.api.aswemakeapitask.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.aswemake.api.aswemakeapitask.exception.ErrorMessages.COUPON_ALREADY_USED;
import static com.aswemake.api.aswemakeapitask.exception.ErrorMessages.COUPON_DISCOUNTED_AMOUNT_EXCEEDS_ORDER_VALUE;
import static com.aswemake.api.aswemakeapitask.exception.ErrorMessages.COUPON_DISCOUNT_CANNOT_EXCEED_100;
import static com.aswemake.api.aswemakeapitask.exception.ErrorMessages.COUPON_NOT_AVAILABLE;
import static com.aswemake.api.aswemakeapitask.exception.ErrorMessages.COUPON_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class CouponCalculate {
    private final CouponRepository couponRepository;

    public Long calculateTotalPrice(List<OrderItemRequest> orderItems, Long couponId) {
        if (couponId == null)
            return orderItems.stream()
                    .mapToLong(item -> item.getPrice() * item.getQuantity())
                    .sum();

        Coupon coupon = couponRepository.findByIdWithItemAndOrders(couponId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, COUPON_NOT_FOUND));

        if (Boolean.TRUE.equals(coupon.isUsed()))
            throw new CustomException(HttpStatus.BAD_REQUEST, COUPON_ALREADY_USED);

        CouponScope couponScope = coupon.getCouponScope();
        CouponType couponType = coupon.getCouponType();
        Double discountValue = coupon.getDiscountValue();

        return switch (couponScope) {
            case ALL_ORDER -> calculateTotalPriceWithCoupon(orderItems, couponType, discountValue);
            case SPECIFIC_ITEM -> calculateTotalPriceWithCouponAndItem(orderItems, coupon, couponType, discountValue);
            default -> orderItems.stream()
                    .mapToLong(item -> item.getPrice() * item.getQuantity())
                    .sum();
        };
    }

    private Long calculateTotalPriceWithCoupon(List<OrderItemRequest> orderItems, CouponType couponType, Double discountValue) {
        long originalTotal = orderItems.stream()
                .mapToLong(item -> item.getPrice() * item.getQuantity())
                .sum();

        switch (couponType) {
            case PERCENTAGE:
                if (discountValue > 100) {
                    throw new CustomException(HttpStatus.BAD_REQUEST, COUPON_DISCOUNT_CANNOT_EXCEED_100);
                }
                long discountedAmountPercentage = (long) (originalTotal * (discountValue / 100));
                if (originalTotal - discountedAmountPercentage < 0) {
                    throw new CustomException(HttpStatus.BAD_REQUEST, COUPON_DISCOUNTED_AMOUNT_EXCEEDS_ORDER_VALUE);
                }
                return originalTotal - discountedAmountPercentage;

            case FIXED:
                long discountedAmountFixed = discountValue.longValue();
                if (originalTotal - discountedAmountFixed < 0) {
                    throw new CustomException(HttpStatus.BAD_REQUEST, COUPON_DISCOUNTED_AMOUNT_EXCEEDS_ORDER_VALUE);
                }
                return originalTotal - discountedAmountFixed;

            default:
                throw new CustomException(HttpStatus.BAD_REQUEST, COUPON_NOT_AVAILABLE);
        }
    }

    private Long calculateTotalPriceWithCouponAndItem(List<OrderItemRequest> orderItems, Coupon coupon, CouponType couponType, Double discountValue) {
        long originalTotal = 0;
        long applicableTotal = 0;

        for (OrderItemRequest item : orderItems) {
            long itemTotal = item.getPrice() * item.getQuantity();
            originalTotal += itemTotal;

            if (item.getItemId().equals(coupon.getItem().getId())) {
                applicableTotal += itemTotal;
            }
        }

        switch (couponType) {
            case PERCENTAGE -> {
                if (discountValue > 100) {
                    throw new CustomException(HttpStatus.BAD_REQUEST, COUPON_DISCOUNT_CANNOT_EXCEED_100);
                }
                long discountForApplicableItems = (long) (applicableTotal * (discountValue / 100));
                if (originalTotal - discountForApplicableItems < 0) {
                    throw new CustomException(HttpStatus.BAD_REQUEST, COUPON_DISCOUNTED_AMOUNT_EXCEEDS_ORDER_VALUE);
                }
                return originalTotal - discountForApplicableItems;
            }
            case FIXED -> {
                long discountedAmountFixed = discountValue.longValue();
                if (originalTotal - discountedAmountFixed < 0) {
                    throw new CustomException(HttpStatus.BAD_REQUEST, COUPON_DISCOUNTED_AMOUNT_EXCEEDS_ORDER_VALUE);
                }
                return originalTotal - discountedAmountFixed;
            }
            default -> throw new IllegalArgumentException("Unsupported coupon type");
        }
    }

}

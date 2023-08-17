package com.aswemake.api.aswemakeapitask.controller;


import com.aswemake.api.aswemakeapitask.domain.coupon.CouponScope;
import com.aswemake.api.aswemakeapitask.dto.GlobalResponse;
import com.aswemake.api.aswemakeapitask.dto.coupon.response.CouponResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.aswemake.api.aswemakeapitask.domain.coupon.CouponType.FIXED;
import static java.time.LocalDateTime.now;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/coupons")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class CouponController {
    // TODO : GET /coupons/{id}: 쿠폰 정보 조회
    @GetMapping("/{id}")
    public ResponseEntity<GlobalResponse> selectCoupon(@PathVariable Long id) {
        CouponResponseDto couponResponseDto = CouponResponseDto.builder()
                .id(1L)
                .name("coupon name")
                .couponType(FIXED)
                .discountValue(5.0)
                .couponScope(CouponScope.SPECIFIC_ITEM)
                .orderId(1L)
                .itemId(2L)
                .itemName("item name")
                .build();

        return ResponseEntity.ok(GlobalResponse
                .builder()
                .status(HttpStatus.OK)
                .timestamp(now())
                .message("쿠폰 정보 조회 성공")
                .data(couponResponseDto)
                .build());
    }
}

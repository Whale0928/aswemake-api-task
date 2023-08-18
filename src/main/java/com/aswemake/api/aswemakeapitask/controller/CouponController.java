package com.aswemake.api.aswemakeapitask.controller;


import com.aswemake.api.aswemakeapitask.dto.GlobalResponse;
import com.aswemake.api.aswemakeapitask.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/coupons")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class CouponController {
    private final CouponService couponService;

    @GetMapping("/{id}")
    public ResponseEntity<GlobalResponse> selectCoupon(@PathVariable Long id) throws Exception {
        return GlobalResponse.ok("쿠폰 정보 조회 성공", couponService.selectCoupon(id));
    }
}

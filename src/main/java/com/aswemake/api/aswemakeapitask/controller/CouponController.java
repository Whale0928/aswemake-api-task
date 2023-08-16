package com.aswemake.api.aswemakeapitask.controller;


import com.aswemake.api.aswemakeapitask.dto.GlobalResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/conpons")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class CouponController {
    // TODO : GET /coupons/{id}: 쿠폰 정보 조회
    @GetMapping("{id}")
    public ResponseEntity<GlobalResponse> selectCoupon() {
        return null;
    }
}

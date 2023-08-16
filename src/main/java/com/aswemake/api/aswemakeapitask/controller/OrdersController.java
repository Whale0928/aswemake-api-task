package com.aswemake.api.aswemakeapitask.controller;


import com.aswemake.api.aswemakeapitask.dto.GlobalResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/orders")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class OrdersController {
    // TODO : POST /orders: 주문 생성
    @PostMapping
    public ResponseEntity<GlobalResponse> createOrder() {
        return null;
    }

    // TODO : GET /orders/{id}: 주문 정보 조회
    @GetMapping("{id}")
    public ResponseEntity<GlobalResponse> selectOrder() {
        return null;
    }

    // TODO : GET /orders/{id}/total: 주문에 대한 총 금액 계산
    @GetMapping("{id}/total")
    public ResponseEntity<GlobalResponse> calculateTotalPrice() {
        return null;
    }

    @GetMapping("{id}/payment")
    public ResponseEntity<GlobalResponse> calculatePaymentPrice() {
        // TODO : GET /orders/{id}/payment: 주문에 대한 필요 결제 금액 계산
        return null;
    }
}

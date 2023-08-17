package com.aswemake.api.aswemakeapitask.controller;


import com.aswemake.api.aswemakeapitask.domain.orders.OrderStatus;
import com.aswemake.api.aswemakeapitask.dto.GlobalResponse;
import com.aswemake.api.aswemakeapitask.dto.orders.request.OrderCalculateTotalPriceRequestDto;
import com.aswemake.api.aswemakeapitask.dto.orders.request.OrderCreateRequestDto;
import com.aswemake.api.aswemakeapitask.dto.orders.response.OrderCreateResponseDto;
import com.aswemake.api.aswemakeapitask.dto.orders.response.OrderItemDto;
import com.aswemake.api.aswemakeapitask.dto.orders.response.OrderSelectResponseDto;
import com.aswemake.api.aswemakeapitask.dto.users.response.UserLoginInfo;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static java.time.LocalDateTime.now;
import static java.util.Arrays.asList;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/orders")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class OrdersController {

    // TODO : POST /orders: 주문 생성
    @PostMapping
    public ResponseEntity<GlobalResponse> createOrder(@Valid @RequestBody OrderCreateRequestDto orderCreateRequestDto
            , HttpSession session) {

        // 세션에서 사용자 정보 가져오기
        UserLoginInfo userInfo = (UserLoginInfo) session.getAttribute("userInfo");

        // 세션에서 사용자 정보가 없는 경우 (로그인하지 않은 경우)
        if (userInfo.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(GlobalResponse.builder()
                            .status(HttpStatus.UNAUTHORIZED)
                            .message("로그인이 필요합니다.")
                            .build());
        }

        // 주문 생성에 성공한 경우 응답 반환
        return GlobalResponse.ok("주문이 생성되었습니다.",
                OrderCreateResponseDto.builder()
                        .id(1L)
                        .orderCode("2021090001-market-AA")
                        .orderStatus(OrderStatus.PAID)
                        .totalAmount(150_000L)
                        .orderDate(now())
                        .build());
    }

    // TODO : GET /orders/{id}: 주문 정보 조회
    @GetMapping("{id}")
    public ResponseEntity<GlobalResponse> selectOrder(@PathVariable Long id, HttpSession session) {
        List<OrderItemDto> orderItemDtoList = asList(OrderItemDto.builder()
                        .id(1L)
                        .name("name")
                        .quantity(1)
                        .price(1L)
                        .build(),
                OrderItemDto.builder()
                        .id(1L)
                        .name("name")
                        .quantity(1)
                        .price(1L)
                        .build());

        OrderSelectResponseDto responseDto = OrderSelectResponseDto.builder()
                .orderCode("orderCode")
                .userId(1L)
                .userName("userName")
                .status(OrderStatus.PAID)
                .totalAmount(150_000L)
                .deliveryFee(3_000L)
                .orderItems(orderItemDtoList)
                .build();

        return GlobalResponse.ok("주문을 조회하였습니다.", responseDto);
    }

    // TODO : GET /orders/total: 주문에 대한 총 금액 계산 - 결제전
    @GetMapping("/total")
    public ResponseEntity<GlobalResponse> calculateTotalPrice(@Valid @RequestBody OrderCalculateTotalPriceRequestDto orderCalculateTotalPriceRequestDto) {
        Integer totalAmount = 150_000;
        return GlobalResponse.ok("주문에 대한 총 금액 : ", totalAmount);
    }

    // TODO : GET /orders/{id}/amount: 주문에 대한 필요 결제 금액 계산 - 결제 후
    @GetMapping("{id}/amount")
    public ResponseEntity<GlobalResponse> calculatePaymentPrice(@PathVariable Long id) {
        Integer totalAmount = 150_000;
        return GlobalResponse.ok("주문에 대한 총 금액 : ", totalAmount);
    }
}

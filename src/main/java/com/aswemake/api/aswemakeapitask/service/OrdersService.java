package com.aswemake.api.aswemakeapitask.service;

import com.aswemake.api.aswemakeapitask.domain.orders.OrderStatus;
import com.aswemake.api.aswemakeapitask.domain.orders.Orders;
import com.aswemake.api.aswemakeapitask.domain.orders.OrdersRepository;
import com.aswemake.api.aswemakeapitask.domain.user.UserRepository;
import com.aswemake.api.aswemakeapitask.domain.user.Users;
import com.aswemake.api.aswemakeapitask.dto.orders.request.OrderCreateRequestDto;
import com.aswemake.api.aswemakeapitask.dto.orders.response.OrderCreateResponseDto;
import com.aswemake.api.aswemakeapitask.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import static com.aswemake.api.aswemakeapitask.exception.ErrorMessages.USER_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrdersService {
    private final OrdersRepository ordersRepository;
    private final UserRepository userRepository;
    private final CouponCalculate couponCalculate;

    public OrderCreateResponseDto createOrder(OrderCreateRequestDto orderCreateRequestDto, Long userId) throws Exception {
        log.info("createOrder: orderCreateRequestDto={}, userId={}", orderCreateRequestDto, userId);

        //주문자 조회
        Users users = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, USER_NOT_FOUND));

        //주문 번호 생성
        String orderCode = createOrderCode();

        //주문 아이템들 분석
        //여기서 뭘해야할까.
        //- 재고 감소

        //총 결제 금액 계산
        //- 쿠폰 사용
        Long totalAmount = couponCalculate.calculateTotalPrice(orderCreateRequestDto.getOrderItems(), orderCreateRequestDto.getCouponId());

        //주문 생성 및 저장
        Orders orders = ordersRepository.saveAndFlush(
                Orders.builder()
                        .orderCode(orderCode)
                        .users(users)
                        .status(OrderStatus.WAITING)
                        .zipCode(orderCreateRequestDto.getZipCode())
                        .address(orderCreateRequestDto.getAddress())
                        .packingType(orderCreateRequestDto.getPackingType())
                        .totalAmount(totalAmount)
                        .deliveryFee(orderCreateRequestDto.getDeliveryFee())
                        .build()
        );

        //응답 객체 반환
        return OrderCreateResponseDto.builder()
                .id(orders.getId())
                .orderCode(orders.getOrderCode())
                .orderStatus(orders.getStatus())
                .totalAmount(orders.getTotalAmount())
                .deliveryFee(orders.getDeliveryFee())
                .zipCode(orders.getZipCode())
                .address(orders.getAddress())
                .orderDate(orders.getCreatedDate())
                .build();
    }

    public String createOrderCode() {
        return "2021090001-market-AA";
    }


}


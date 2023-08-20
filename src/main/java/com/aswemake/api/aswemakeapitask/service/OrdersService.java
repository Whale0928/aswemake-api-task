package com.aswemake.api.aswemakeapitask.service;

import com.aswemake.api.aswemakeapitask.domain.orders.OrderStatus;
import com.aswemake.api.aswemakeapitask.domain.orders.Orders;
import com.aswemake.api.aswemakeapitask.domain.orders.OrdersRepository;
import com.aswemake.api.aswemakeapitask.domain.user.UserRepository;
import com.aswemake.api.aswemakeapitask.domain.user.Users;
import com.aswemake.api.aswemakeapitask.dto.orders.request.OrderCalculateTotalPriceRequestDto;
import com.aswemake.api.aswemakeapitask.dto.orders.request.OrderCreateRequestDto;
import com.aswemake.api.aswemakeapitask.dto.orders.response.OrderCreateResponseDto;
import com.aswemake.api.aswemakeapitask.dto.orders.response.OrderItemDto;
import com.aswemake.api.aswemakeapitask.dto.orders.response.OrderSelectResponseDto;
import com.aswemake.api.aswemakeapitask.exception.CustomException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.aswemake.api.aswemakeapitask.exception.ErrorMessages.ORDER_NOT_FOUND;
import static com.aswemake.api.aswemakeapitask.exception.ErrorMessages.USER_NOT_FOUND;
import static com.aswemake.api.aswemakeapitask.service.CodeGenerator.createOrderCode;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrdersService {
    private final OrdersRepository ordersRepository;
    private final UserRepository userRepository;
    private final CouponCalculate couponCalculate;
    private final ItemStockService itemStockService;

    private static Orders getBuild(OrderCreateRequestDto orderCreateRequestDto, String orderCode, Users users, Long totalAmount) {
        return Orders.builder()
                .orderCode(orderCode)
                .users(users)
                .status(OrderStatus.WAITING)
                .zipCode(orderCreateRequestDto.getZipCode())
                .address(orderCreateRequestDto.getAddress())
                .packingType(orderCreateRequestDto.getPackingType())
                .totalAmount(totalAmount)
                .deliveryFee(orderCreateRequestDto.getDeliveryFee())
                .build();
    }

    private static OrderCreateResponseDto getOrderCreateResponseDto(Orders orders) {
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

    @Transactional
    public OrderCreateResponseDto createOrder(OrderCreateRequestDto orderCreateRequestDto, Long userId) throws Exception {
        log.info("createOrder: orderCreateRequestDto={}, userId={}", orderCreateRequestDto, userId);

        //주문자 조회
        Users users = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, USER_NOT_FOUND));

        //주문 번호 생성
        String orderCode = createOrderCode(users.getId());

        //재고 감소
        itemStockService.decreaseStock(orderCreateRequestDto.getOrderItems());

        //총 결제 금액 계산
        //- 쿠폰 사용
        Long totalAmount = couponCalculate.calculateTotalPrice(orderCreateRequestDto.getOrderItems(), orderCreateRequestDto.getCouponId());

        //주문 생성 및 저장
        Orders orders = ordersRepository.saveAndFlush(
                getBuild(orderCreateRequestDto, orderCode, users, totalAmount)
        );

        //응답 객체 반환
        return getOrderCreateResponseDto(orders);
    }

    public OrderSelectResponseDto selectOrder(Long id) {
        Orders orders = ordersRepository.findByIdForSelectOrder(id)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, ORDER_NOT_FOUND));

        List<OrderItemDto> list = orders.getOrderItems().stream().map(orderItem -> OrderItemDto.builder()
                .id(orderItem.getId())
                .name(orderItem.getItem().getName())
                .quantity(orderItem.getQuantity())
                .price(orderItem.getItem().getPrice())
                .build()).toList();

        return OrderSelectResponseDto.builder()
                .orderCode(orders.getOrderCode())
                .userId(orders.getUsers().getId())
                .userName(orders.getUsers().getName())
                .status(orders.getStatus())
                .totalAmount(orders.getTotalAmount())
                .deliveryFee(orders.getDeliveryFee())
                .orderItems(list)
                .build();
    }

    public Long calculateTotalPrice(OrderCalculateTotalPriceRequestDto totalPriceRequestDto) {
        return totalPriceRequestDto.getOrderItems().stream().mapToLong(item -> item.getPrice() * item.getQuantity()).sum();
    }

    public Long calculatePaymentPrice(Long id) {
        Orders orders = ordersRepository.findByIdForSelectOrder(id)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, ORDER_NOT_FOUND));
        return orders.getOrderItems()
                .stream()
                .mapToLong(oItem -> oItem.getItem().getPrice() * oItem.getQuantity())
                .sum();
    }
}


package com.aswemake.api.aswemakeapitask.dto.orders.response;


import com.aswemake.api.aswemakeapitask.domain.orders.OrderStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderSelectResponseDto {
    private String orderCode;
    private Long userId;
    private String userName;
    private OrderStatus status;
    private Long totalAmount;
    private Long deliveryFee;
    private List<OrderItemDto> orderItems;
}
package com.aswemake.api.aswemakeapitask.dto.orders.request;


import jakarta.validation.constraints.NotEmpty;
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
public class OrderCalculateTotalPriceRequestDto {
    @NotEmpty(message = "주문 상품 목록은 비어있을 수 없습니다.")
    private List<OrderItemRequest> orderItems;
}

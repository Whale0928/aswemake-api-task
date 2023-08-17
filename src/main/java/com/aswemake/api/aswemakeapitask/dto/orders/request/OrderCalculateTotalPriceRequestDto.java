package com.aswemake.api.aswemakeapitask.dto.orders.request;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class OrderItemRequest {
        @NotNull(message = "상품 ID는 필수입니다.")
        private Long itemId;

        @Min(value = 1, message = "수량은 1 이상이어야 합니다.")
        private int quantity;
    }
}

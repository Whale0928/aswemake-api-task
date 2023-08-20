package com.aswemake.api.aswemakeapitask.dto.orders.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderItemRequest {
    @NotNull(message = "상품 ID는 필수입니다.")
    private Long itemId;

    @NotNull(message = "상품 단가는 필수입니다.")
    private Long price;

    @Min(value = 1, message = "수량은 1 이상이어야 합니다.")
    @NotNull(message = "수량은 필수입니다.")
    private int quantity;

    public static OrderItemRequest of(Long itemId, Long price, int quantity) {
        return OrderItemRequest.builder()
                .itemId(itemId)
                .price(price)
                .quantity(quantity)
                .build();
    }
}
package com.aswemake.api.aswemakeapitask.dto.orders.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderItemDto {
    private Long id;
    private String name;
    private int quantity;
    private Long price;
}


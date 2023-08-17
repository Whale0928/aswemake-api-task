package com.aswemake.api.aswemakeapitask.dto.item.response;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemSelectResponseDto {
    private Long id;
    private String name;
    private int price;
    private int stockQuantity;
    private int remainingStockQuantity;   //남은 재고 수량
}

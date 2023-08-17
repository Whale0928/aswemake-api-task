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
public class ItemUpdateResponseDto {
    private Long id;
    private String name;
    private int beforePrice; //수정 전 가격
    private int afterPrice; //수정 후 가격
    private int stockQuantity;
    private int remainingStockQuantity;   //수정 당시 남은 재고 수량
}

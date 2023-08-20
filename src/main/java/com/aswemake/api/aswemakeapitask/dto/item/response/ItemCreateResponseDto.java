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
public class ItemCreateResponseDto {
    private Long id;
    private String name;
    private Long price;
    private int stockQuantity;
}

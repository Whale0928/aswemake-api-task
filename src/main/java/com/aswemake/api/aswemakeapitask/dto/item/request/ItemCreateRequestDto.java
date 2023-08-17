package com.aswemake.api.aswemakeapitask.dto.item.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemCreateRequestDto {

    @NotBlank(message = "상품 이름은 필수입니다.")
    @Size(min = 2, message = "상품 이름은 최소 2자 이상이어야 합니다.")
    private String name;

    @NotNull(message = "상품 가격은 필수입니다.")
    @Min(value = 1, message = "상품 가격은 0보다 커야 합니다.")
    private int price;

    @NotNull(message = "상품 재고 수량은 필수입니다.")
    @Min(value = 0, message = "상품 재고 수량은 0 이상이어야 합니다.")
    private int stockQuantity;
}

package com.aswemake.api.aswemakeapitask.dto.item.request;

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
public class ItemUpdateRequestDto {

    @NotNull(message = "수정 대상 가격은 필수입니다.")
    @Min(value = 1, message = "상품 가격은 0보다 커야 합니다.")
    private int price;
}

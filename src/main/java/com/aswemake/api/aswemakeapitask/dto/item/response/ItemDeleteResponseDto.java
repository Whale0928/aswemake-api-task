package com.aswemake.api.aswemakeapitask.dto.item.response;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemDeleteResponseDto {
    private Long id;
    private String name;
    private int remainingStockQuantity;
    private LocalDateTime deletedAt;    //삭제 일시
}

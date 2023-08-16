package com.aswemake.api.aswemakeapitask.domain.item;

import com.aswemake.api.aswemakeapitask.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PriceHistory extends BaseEntity {

    // 관련 상품 정보
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    // 해당 시점에서의 상품 가격
    private Long price;

    // 가격이 변경된 날짜 및 시간
    private LocalDateTime changedDate;
}
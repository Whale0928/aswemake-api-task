package com.aswemake.api.aswemakeapitask.domain.item;

import com.aswemake.api.aswemakeapitask.common.entity.BaseEntity;
import com.aswemake.api.aswemakeapitask.domain.coupon.Coupon;
import com.aswemake.api.aswemakeapitask.domain.orders.OrderItem;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Item extends BaseEntity {
    // 상품 이름
    private String name;
    // 상품 가격
    private Long price;
    // 상품 재고 수량
    private int stockQuantity;

    // 주문한 상품들의 목록
    @Builder.Default
    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems = new ArrayList<>();

    // 해당 상품만 할인하는 쿠폰 목록
    @Builder.Default
    @OneToMany(mappedBy = "item", fetch = FetchType.LAZY)
    private List<Coupon> coupons = new ArrayList<>();

    // 가격 변동 이력
    @Builder.Default
    @OneToMany(mappedBy = "item", fetch = FetchType.LAZY)
    private List<PriceHistory> priceHistories = new ArrayList<>();
}
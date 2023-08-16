package com.aswemake.api.aswemakeapitask.domain.coupon;


import com.aswemake.api.aswemakeapitask.common.entity.BaseEntity;
import com.aswemake.api.aswemakeapitask.domain.item.Item;
import com.aswemake.api.aswemakeapitask.domain.orders.Orders;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Coupon extends BaseEntity {
    // 쿠폰 이름
    private String name;

    // 쿠폰 적용 방법 (비율 또는 고정)
    @Enumerated(EnumType.STRING)
    private CouponType couponType;

    // 적용 비율 또는 적용 금액
    private Double discountValue;

    // 적용 범위 ( 또는 특정 상품 한정)
    // 주문 전체(배달비 제외)
    // 특정 상품 한정 (특정 상품의 모든 개수에 적용)
    @Enumerated(EnumType.STRING)
    private CouponScope couponScope;

    // 사용된 주문
    @OneToOne(mappedBy = "coupon", cascade = CascadeType.ALL)
    private Orders orders;

    // 특정 상품 한정 쿠폰의 경우, 해당 상품 정보
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    public void addItem(Item item) {
        this.item = item;
        item.getCoupons().add(this);
    }

    public void removeItem() {
        item.getCoupons().remove(this);
        this.item = null;
    }
}

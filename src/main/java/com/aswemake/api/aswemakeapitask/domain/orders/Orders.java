package com.aswemake.api.aswemakeapitask.domain.orders;

import com.aswemake.api.aswemakeapitask.common.entity.BaseEntity;
import com.aswemake.api.aswemakeapitask.domain.coupon.Coupon;
import com.aswemake.api.aswemakeapitask.domain.user.Users;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Orders extends BaseEntity {

    //주문 번호 (비지니스 키)
    private String orderCode;

    // 주문한 사용자 정보
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users users;

    // 주문 상태
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    // 배송지 정보
    private String zipCode;
    private String address;

    // 포장 타입(큐백 or 일반)
    @Enumerated(EnumType.STRING)
    private PackingType packingType;

    // 총 결제 금액
    private Long totalAmount;

    // 배달비
    private Long deliveryFee;

    // 주문한 상품들의 목록
    @Builder.Default
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    // 적용된 쿠폰 정보
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;
}
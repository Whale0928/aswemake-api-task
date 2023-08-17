package com.aswemake.api.aswemakeapitask.domain.orders;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderStatus {
    WAITING("주문 대기"),
    PAID("결제 완료"),
    CANCELLED("주문 취소");

    private final String status;
}

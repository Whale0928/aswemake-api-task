package com.aswemake.api.aswemakeapitask.dto.orders.response;


import com.aswemake.api.aswemakeapitask.domain.orders.OrderStatus;
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
public class OrderCreateResponseDto {
    private Long id; // 생성된 주문의 ID
    private String orderCode; // 생성된 주문의 ID
    private OrderStatus orderStatus; // 주문 상태 (예: "주문 완료")
    private Long totalAmount; // 총 결제 금액
    private LocalDateTime orderDate; // 주문 날짜 및 시간
}

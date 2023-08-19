package com.aswemake.api.aswemakeapitask.dto.orders.request;


import com.aswemake.api.aswemakeapitask.domain.orders.PackingType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderCreateRequestDto {

    @NotEmpty(message = "주문 상품 목록은 비어있을 수 없습니다.")
    private List<OrderItemRequest> orderItems;

    private Long couponId;

    @Pattern(regexp = "^[0-9]{5,6}$", message = "배송지 우편번호는 5~6자리의 숫자여야 합니다.")
    @NotNull(message = "배송지 우편번호는 필수입니다.")
    private String zipCode;

    @Pattern(regexp = "^.{10,}$", message = "배송지 주소는 최소 10자리 이상이어야 합니다.")
    @NotNull(message = "배송지 주소는 필수입니다.")
    private String address;

    @NotNull(message = "배달비는 필수입니다. (배달비가 없는 경우 0을 입력하세요.)")
    private Long deliveryFee;

    @NotNull(message = "포장 타입은 필수입니다.")
    private PackingType packingType;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class OrderItemRequest {
        @NotNull(message = "상품 ID는 필수입니다.")
        private Long itemId;

        @NotNull(message = "상품 단가는 필수입니다.")
        private Long price;

        @Min(value = 1, message = "수량은 1 이상이어야 합니다.")
        @NotNull(message = "수량은 필수입니다.")
        private int quantity;
    }
}

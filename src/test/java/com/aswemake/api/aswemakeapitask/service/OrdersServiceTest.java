package com.aswemake.api.aswemakeapitask.service;

import com.aswemake.api.aswemakeapitask.domain.coupon.Coupon;
import com.aswemake.api.aswemakeapitask.domain.coupon.CouponRepository;
import com.aswemake.api.aswemakeapitask.domain.coupon.CouponScope;
import com.aswemake.api.aswemakeapitask.domain.coupon.CouponType;
import com.aswemake.api.aswemakeapitask.domain.item.Item;
import com.aswemake.api.aswemakeapitask.domain.orders.OrderStatus;
import com.aswemake.api.aswemakeapitask.domain.orders.Orders;
import com.aswemake.api.aswemakeapitask.domain.orders.PackingType;
import com.aswemake.api.aswemakeapitask.dto.orders.request.OrderItemRequest;
import com.aswemake.api.aswemakeapitask.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@ExtendWith(MockitoExtension.class)
class CouponCalculateTest {

    @InjectMocks
    private CouponCalculate couponCalculate;
    @Mock
    private CouponRepository couponRepository;


    private List<OrderItemRequest> createOrderItems() {
        return List.of(
                OrderItemRequest.of(1L, 1000L, 1),
                OrderItemRequest.of(2L, 2000L, 2),
                OrderItemRequest.of(3L, 3000L, 3),
                OrderItemRequest.of(4L, 4000L, 4),
                OrderItemRequest.of(5L, 5000L, 5)
        );
        /*
        1 * 1000L = 1000L
        2 * 2000L = 4000L
        3 * 3000L = 9000L
        4 * 4000L = 16000L
        5 * 5000L = 25000L
        1000L + 4000L + 9000L + 16000L + 25000L = 55000L
        So, the total sum is 55,000L.
        */
    }

    @Test
    @DisplayName("쿠폰 사용시 총 결제 금액 계산")
    void calculateTotalPriceTest_NotUsedCoupon() {
        List<OrderItemRequest> orderItems = createOrderItems();

        // When
        Long totalAmount = orderItems.stream()
                .mapToLong(item -> item.getPrice() * item.getQuantity())
                .sum();
        // Then
        assertThat(totalAmount).isEqualTo(55_000);
    }

    @Test
    @DisplayName("쿠폰 사용시 총 결제 금액 계산")
    void calculateTotalPriceTest() {
        Long couponId = 1L;
        // Given
        Coupon coupon = Coupon.builder()
                .name("10% off")
                .couponType(CouponType.PERCENTAGE) // 비율 할인
                .discountValue(10.0)
                .couponScope(CouponScope.ALL_ORDER) // 전체 주문
                .build();
        when(couponRepository.findByIdWithItemAndOrders(couponId)).thenReturn(Optional.of(coupon));
        List<OrderItemRequest> orderItems = createOrderItems();

        // When
        Long totalAmount = couponCalculate.calculateTotalPrice(orderItems, couponId);

        // Then
        assertThat(totalAmount).isEqualTo(55_000L - (long) (55_000L * (10.0 / 100)));
    }

    @Test
    @DisplayName("금액 할인 쿠폰 사용시 총 결제 금액 계산")
    void calculateTotalPriceTest_AmountCoupon() {
        Long couponId = 2L;
        // Given
        Coupon coupon = Coupon.builder()
                .name("5000 off")
                .couponType(CouponType.FIXED) // 금액 할인
                .discountValue(5000.0)
                .couponScope(CouponScope.ALL_ORDER) // 전체 주문
                .build();
        when(couponRepository.findByIdWithItemAndOrders(couponId)).thenReturn(Optional.of(coupon));
        List<OrderItemRequest> orderItems = createOrderItems();

        // When
        Long totalAmount = couponCalculate.calculateTotalPrice(orderItems, couponId);

        // Then
        assertThat(totalAmount).isEqualTo(55_000L - 5000L);
    }

    @Test
    @DisplayName("특정 아이템에만 쿠폰 적용시 총 결제 금액 계산")
    void calculateTotalPriceTest_ItemSpecificCoupon() {
        Long couponId = 3L;

        Item specificItem = Item.builder()
                .name("Specific Item")
                .price(1000L)
                .stockQuantity(100)
                .build();

        setField(specificItem, "id", 1L);

        // Given
        Coupon coupon = Coupon.builder()
                .name("Item 1 50% off")
                .couponType(CouponType.PERCENTAGE)  // 비율 할인
                .discountValue(50.0)
                .couponScope(CouponScope.SPECIFIC_ITEM)  // 특정 아이템
                .item(specificItem)
                .build();

        // Correct the mocking behavior here
        when(couponRepository.findByIdWithItemAndOrders(couponId)).thenReturn(Optional.of(coupon));

        List<OrderItemRequest> orderItems = createOrderItems();

        // When
        Long totalAmount = couponCalculate.calculateTotalPrice(orderItems, couponId);

        Long expectedDiscountedValue = (long) (orderItems.get(0).getPrice() * (50.0 / 100));
        assertThat(totalAmount).isEqualTo(55_000L - expectedDiscountedValue);
    }

    @Test
    @DisplayName("100% 초과 할인율 적용시 오류 발생")
    void calculateTotalPriceTest_Over100PercentDiscount() {
        Long couponId = 4L;

        Coupon coupon = Coupon.builder()
                .name("110% off")
                .couponType(CouponType.PERCENTAGE)
                .discountValue(110.0)
                .couponScope(CouponScope.ALL_ORDER)
                .build();

        when(couponRepository.findByIdWithItemAndOrders(couponId)).thenReturn(Optional.of(coupon));
        List<OrderItemRequest> orderItems = createOrderItems();

        // When & Then
        assertThrows(CustomException.class, () -> {
            couponCalculate.calculateTotalPrice(orderItems, couponId);
        });
    }

    @Test
    @DisplayName("쿠폰 할인 금액이 주문 금액보다 클 때 오류 발생")
    void calculateTotalPriceTest_CouponValueExceedsOrderValue() {
        Long couponId = 5L;

        Coupon coupon = Coupon.builder()
                .name("60,000 off")
                .couponType(CouponType.FIXED)
                .discountValue(60_000.0)
                .couponScope(CouponScope.ALL_ORDER)
                .build();

        when(couponRepository.findByIdWithItemAndOrders(couponId)).thenReturn(Optional.of(coupon));
        List<OrderItemRequest> orderItems = createOrderItems();

        // When & Then
        assertThrows(CustomException.class, () -> {
            couponCalculate.calculateTotalPrice(orderItems, couponId);
        });
    }

    @Test
    @DisplayName("이미 사용된 쿠폰 사용 시도 시 오류 발생")
    void calculateTotalPriceTest_UsedCoupon() {
        Long couponId = 6L;

        Orders usedOrder = Orders.builder()
                .orderCode("12345")
                .status(OrderStatus.PAID)
                .zipCode("123456")
                .address("Test Address")
                .packingType(PackingType.Q_BAG)
                .totalAmount(100000L)
                .deliveryFee(5000L)
                .build();

        Coupon coupon = Coupon.builder()
                .name("10% off")
                .couponType(CouponType.PERCENTAGE)
                .discountValue(10.0)
                .couponScope(CouponScope.ALL_ORDER)
                .orders(usedOrder)
                .build();

        when(couponRepository.findByIdWithItemAndOrders(couponId)).thenReturn(Optional.of(coupon));
        List<OrderItemRequest> orderItems = createOrderItems();

        // When & Then
        assertThrows(CustomException.class, () -> {
            couponCalculate.calculateTotalPrice(orderItems, couponId);
        });
    }
}
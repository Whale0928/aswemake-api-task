package com.aswemake.api.aswemakeapitask.service;

import com.aswemake.api.aswemakeapitask.domain.coupon.Coupon;
import com.aswemake.api.aswemakeapitask.domain.coupon.CouponRepository;
import com.aswemake.api.aswemakeapitask.domain.coupon.CouponScope;
import com.aswemake.api.aswemakeapitask.domain.coupon.CouponType;
import com.aswemake.api.aswemakeapitask.dto.coupon.response.CouponResponseDto;
import com.aswemake.api.aswemakeapitask.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@ExtendWith(MockitoExtension.class)
class CouponServiceTest {

    @InjectMocks
    private CouponService couponService;

    @Mock
    private CouponRepository couponRepository;

    private Coupon coupon;

    @BeforeEach
    void setUp() {
        coupon = Coupon.builder()
                .name("10% off")
                .couponType(CouponType.PERCENTAGE)
                .discountValue(10.0)
                .couponScope(CouponScope.ALL_ORDER)
                .build();
        setField(coupon, "id", 1L);
    }

    @Test
    @DisplayName("쿠폰을 성공적으로 조회하는 경우")
    void selectCoupon_Success() {
        Long couponId = 1L;
        when(couponRepository.findByIdWithItemAndOrders(couponId)).thenReturn(Optional.of(coupon));

        // When
        CouponResponseDto responseDto = couponService.selectCoupon(couponId);

        // Then
        assertThat(responseDto.getId()).isEqualTo(coupon.getId());
        assertThat(responseDto.getName()).isEqualTo(coupon.getName());
        assertThat(responseDto.getCouponType()).isEqualTo(coupon.getCouponType());
        assertThat(responseDto.getDiscountValue()).isEqualTo(coupon.getDiscountValue());
        assertThat(responseDto.getCouponScope()).isEqualTo(coupon.getCouponScope());
        assertThat(responseDto.getOrderId()).isNull();
        assertThat(responseDto.getItemId()).isNull();
        assertThat(responseDto.getItemName()).isNull();
    }

    @Test
    @DisplayName("존재하지 않는 쿠폰 조회 시 예외 발생")
    void selectCoupon_NotFound() {
        Long couponId = 1L;
        // Given
        when(couponRepository.findByIdWithItemAndOrders(couponId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(CustomException.class, () -> {
            couponService.selectCoupon(couponId);
        });
    }

}
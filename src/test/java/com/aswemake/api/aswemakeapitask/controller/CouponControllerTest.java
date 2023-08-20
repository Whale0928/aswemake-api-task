package com.aswemake.api.aswemakeapitask.controller;


import com.aswemake.api.aswemakeapitask.domain.coupon.CouponScope;
import com.aswemake.api.aswemakeapitask.dto.coupon.response.CouponResponseDto;
import com.aswemake.api.aswemakeapitask.exception.CustomException;
import com.aswemake.api.aswemakeapitask.restDocs.RestDocsSupport;
import com.aswemake.api.aswemakeapitask.service.CouponService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.aswemake.api.aswemakeapitask.domain.coupon.CouponType.FIXED;
import static com.aswemake.api.aswemakeapitask.exception.ErrorMessages.COUPON_NOT_FOUND;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CouponControllerTest extends RestDocsSupport {

    @InjectMocks
    private CouponController couponController;

    @Mock
    private CouponService couponService;

    @Override
    protected Object initController() {
        return new CouponController(couponService);
    }

    @Test
    @DisplayName("존재하는 쿠폰 ID로 쿠폰 정보 조회")
    public void selectCoupon_With_Valid_Id() throws Exception {
        Long id = 1L;
        CouponResponseDto responseDto = CouponResponseDto
                .builder()
                .id(1L)
                .name("coupon name")
                .couponType(FIXED)
                .discountValue(5.0)
                .couponScope(CouponScope.SPECIFIC_ITEM)
                .orderId(1L)
                .itemId(2L)
                .itemName("item name")
                .build();


        when(couponService.selectCoupon(1L)).thenReturn(responseDto);

        mockMvc.perform(get("/v1/coupons/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.name").value("coupon name"))
                .andExpect(jsonPath("$.data.couponType").value("FIXED"))
                .andExpect(jsonPath("$.data.discountValue").value(5.0))
                .andExpect(jsonPath("$.data.couponScope").value("SPECIFIC_ITEM"))
                .andExpect(jsonPath("$.data.orderId").value(1L));
    }

    @Test
    @DisplayName("존재하지 않는 쿠폰 ID로 쿠폰 정보 조회")
    public void selectCoupon_With_Invalid_Id() throws Exception {
        when(couponService.selectCoupon(999L)).thenThrow(new CustomException(NOT_FOUND, COUPON_NOT_FOUND));

        mockMvc.perform(get("/v1/coupons/999"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("쿠폰을 찾을 수 없습니다."));
    }

    @Test
    @DisplayName("잘못된 형식의 쿠폰 ID로 쿠폰 정보 조회")
    public void selectCoupon_With_Invalid_Id_Format() throws Exception {
        // 문자열 형식의 쿠폰 ID로 요청
        String invalidId = "invalidId";

        mockMvc.perform(get("/v1/coupons/" + invalidId))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("잘못된 요청 파라미터 타입입니다")));
    }
}
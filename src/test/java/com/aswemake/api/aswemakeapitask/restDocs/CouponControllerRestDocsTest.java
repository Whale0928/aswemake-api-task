package com.aswemake.api.aswemakeapitask.restDocs;

import com.aswemake.api.aswemakeapitask.controller.CouponController;
import com.aswemake.api.aswemakeapitask.domain.coupon.CouponScope;
import com.aswemake.api.aswemakeapitask.dto.GlobalResponse;
import com.aswemake.api.aswemakeapitask.dto.coupon.response.CouponResponseDto;
import com.aswemake.api.aswemakeapitask.service.CouponService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.restdocs.payload.JsonFieldType;

import static com.aswemake.api.aswemakeapitask.domain.coupon.CouponType.FIXED;
import static java.time.LocalDateTime.now;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CouponControllerRestDocsTest extends RestDocsSupport {
    @InjectMocks
    private CouponController couponController;

    @Mock
    private CouponService couponService;

    @Override
    protected Object initController() {
        return new CouponController(couponService);
    }

    @Test
    @DisplayName("쿠폰 정보 조회")
    void selectCoupon() throws Exception {
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

        GlobalResponse response = GlobalResponse
                .builder()
                .status(HttpStatus.OK)
                .timestamp(now())
                .message("쿠폰 정보 조회 성공")
                .data(responseDto)
                .build();
        when(couponService.selectCoupon(id)).thenReturn(responseDto);
        mockMvc.perform(get("/v1/coupons/{id}", id)
                        .pathInfo("/v1/coupons/1")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("coupon/select",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("쿠폰 아이디")
                        ),
                        responseFields(
                                fieldWithPath("status").ignored(),
                                fieldWithPath("timestamp").ignored(),
                                fieldWithPath("message").ignored(),
                                fieldWithPath("data").ignored(),
                                fieldWithPath("data.id").description("쿠폰 아이디").type(JsonFieldType.NUMBER),
                                fieldWithPath("data.name").description("쿠폰 이름"),
                                fieldWithPath("data.couponType").description("쿠폰 적용 방법 (비율 또는 고정)"),
                                fieldWithPath("data.discountValue").description("쿠폰 적용 비율 또는 적용 금액"),
                                fieldWithPath("data.couponScope").description("쿠폰 적용 범위 (모두 또는 특정 상품 한정)"),
                                fieldWithPath("data.orderId").description("사용된 주문").optional().type(JsonFieldType.NUMBER),
                                fieldWithPath("data.itemId").description("특정 상품 아이디").optional().type(JsonFieldType.NUMBER),
                                fieldWithPath("data.itemName").description("특정 상품 이름").optional()
                        )
                ));
    }
}
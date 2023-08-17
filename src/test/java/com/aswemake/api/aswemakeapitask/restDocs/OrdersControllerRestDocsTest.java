package com.aswemake.api.aswemakeapitask.restDocs;

import com.aswemake.api.aswemakeapitask.controller.OrdersController;
import com.aswemake.api.aswemakeapitask.domain.orders.OrderStatus;
import com.aswemake.api.aswemakeapitask.dto.orders.request.OrderCreateRequestDto;
import com.aswemake.api.aswemakeapitask.dto.orders.response.OrderCreateResponseDto;
import com.aswemake.api.aswemakeapitask.dto.users.response.UserLoginInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.restdocs.payload.JsonFieldType;

import java.util.AbstractMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.time.LocalDateTime.now;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class OrdersControllerRestDocsTest extends RestDocsSupport {

    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_NAME = "Test User";
    private static final String TEST_SESSION_ID = "sessionId-test-1234";

    @InjectMocks
    OrdersController ordersController;

    @Override
    protected Object initController() {
        return new OrdersController();
    }

    private UserLoginInfo createTestUserInfo() {
        return UserLoginInfo.builder()
                .id(1L)
                .email(TEST_EMAIL)
                .name(TEST_NAME)
                .sessionId(TEST_SESSION_ID)
                .build();
    }

    private List<OrderCreateRequestDto.OrderItemRequest> createOrderItems() {
        return Stream.of(
                        new AbstractMap.SimpleEntry<>(1L, 5),
                        new AbstractMap.SimpleEntry<>(2L, 10)
                )
                .map(entry -> OrderCreateRequestDto.OrderItemRequest.builder()
                        .itemId(entry.getKey())
                        .quantity(entry.getValue())
                        .build())
                .collect(Collectors.toList());
    }

    @Test
    @DisplayName("주문 생성 성공 시 HTTP 상태 코드 200을 반환해야 한다.")
    void createOrder() throws Exception {
        UserLoginInfo userInfo = createTestUserInfo();
        List<OrderCreateRequestDto.OrderItemRequest> orderItems = createOrderItems();
        OrderCreateRequestDto requestDto = OrderCreateRequestDto.builder()
                .orderItems(orderItems)
                .deliveryFee(5000L)
                .coupons(List.of(1L, 2L))
                .build();

        MockHttpSession mockSession = new MockHttpSession();
        mockSession.setAttribute("userInfo", userInfo);

        OrderCreateResponseDto responseDto = OrderCreateResponseDto.builder()
                .id(1L)
                .orderCode("2021090001-market-AA")
                .orderStatus(OrderStatus.PAID)
                .totalAmount(150_000L)
                .orderDate(now())
                .build();

        mockMvc.perform(post("/v1/orders/{fundingId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .session(mockSession))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("orders/create-order",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("orderItems").description("주문 상품 목록"),
                                fieldWithPath("orderItems[].itemId").description("주문 상품 아이디"),
                                fieldWithPath("orderItems[].quantity").description("주문 상품 수량"),
                                fieldWithPath("coupons").description("사용된 쿠폰 아이디"),
                                fieldWithPath("deliveryFee").description("사용자 비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("status").description("HTTP 상태코드"),
                                fieldWithPath("timestamp").description("응답 시간"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("data").description("응답 데이터"),
                                fieldWithPath("data.id").description("주문 아이디").type(JsonFieldType.NUMBER),
                                fieldWithPath("data.orderCode").description("주문 번호 ( 비지니스_키 ) "),
                                fieldWithPath("data.orderStatus").description("주문 상태"),
                                fieldWithPath("data.totalAmount").description("사용자 이름"),
                                fieldWithPath("data.orderDate").description("구매 일시")
                        )
                ));
    }
}

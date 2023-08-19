package com.aswemake.api.aswemakeapitask.restDocs;

import com.aswemake.api.aswemakeapitask.controller.OrdersController;
import com.aswemake.api.aswemakeapitask.domain.orders.OrderStatus;
import com.aswemake.api.aswemakeapitask.domain.orders.PackingType;
import com.aswemake.api.aswemakeapitask.domain.user.UserRole;
import com.aswemake.api.aswemakeapitask.dto.orders.request.OrderCalculateTotalPriceRequestDto;
import com.aswemake.api.aswemakeapitask.dto.orders.request.OrderCreateRequestDto;
import com.aswemake.api.aswemakeapitask.dto.orders.response.OrderCreateResponseDto;
import com.aswemake.api.aswemakeapitask.dto.orders.response.OrderItemDto;
import com.aswemake.api.aswemakeapitask.dto.orders.response.OrderSelectResponseDto;
import com.aswemake.api.aswemakeapitask.dto.users.response.UserLoginInfo;
import com.aswemake.api.aswemakeapitask.service.OrdersService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.restdocs.payload.JsonFieldType;

import java.util.AbstractMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.time.LocalDateTime.now;
import static java.util.Arrays.asList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class OrdersControllerRestDocsTest extends RestDocsSupport {

    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_NAME = "Test User";
    private static final String TEST_SESSION_ID = "sessionId-test-1234";

    @InjectMocks
    OrdersController ordersController;

    @Mock
    OrdersService ordersService;

    @Override
    protected Object initController() {
        return new OrdersController(ordersService);
    }

    private UserLoginInfo createTestUserInfo() {
        return UserLoginInfo.builder()
                .id(1L)
                .email(TEST_EMAIL)
                .name(TEST_NAME)
                .sessionId(TEST_SESSION_ID)
                .role(UserRole.USER)
                .build();
    }

    private OrderCreateRequestDto.OrderItemRequest createOrderItem(Long id, Long price, int quantity) {
        return OrderCreateRequestDto.OrderItemRequest.builder()
                .itemId(id)
                .price(price)
                .quantity(quantity)
                .build();
    }

    private List<OrderCreateRequestDto.OrderItemRequest> createOrderItems() {
        return List.of(
                createOrderItem(1L, 10_000L, 5),  // 1번 상품의 단가가 10,000원이라 가정
                createOrderItem(2L, 15_000L, 10),  // 2번 상품의 단가가 15,000원이라 가정
                createOrderItem(3L, 20_000L, 2)  // 3번 상품의 단가가 20,000원이라 가정
        );
    }


    private List<OrderCalculateTotalPriceRequestDto.OrderItemRequest> createOrderItemsByTotalPrice() {
        return Stream.of(
                        new AbstractMap.SimpleEntry<>(1L, 5),
                        new AbstractMap.SimpleEntry<>(2L, 10)
                )
                .map(entry -> OrderCalculateTotalPriceRequestDto.OrderItemRequest.builder()
                        .itemId(entry.getKey())
                        .quantity(entry.getValue())
                        .build())
                .collect(Collectors.toList());
    }

    @Test
    @DisplayName("주문 생성")
    void createOrder() throws Exception {

        UserLoginInfo userInfo = createTestUserInfo();

        List<OrderCreateRequestDto.OrderItemRequest> orderItems = createOrderItems();

        // Request DTO
        OrderCreateRequestDto requestDto = OrderCreateRequestDto.builder()
                .orderItems(orderItems)
                .couponId(1L)
                .zipCode("12345")
                .address("서울시 강남구 장난동 101-12")
                .packingType(PackingType.Q_BAG)
                .deliveryFee(5000L)
                .build();

        MockHttpSession mockSession = new MockHttpSession();
        mockSession.setAttribute("userInfo", userInfo);

        OrderCreateResponseDto responseDto = OrderCreateResponseDto.builder()
                .id(1L)
                .orderCode("2021090001-market-AA")
                .orderStatus(OrderStatus.PAID)
                .totalAmount(150_000L)
                .deliveryFee(3_000L)
                .zipCode("12345")
                .address("서울시 강남구 장난동 101-12")
                .orderDate(now())
                .build();

        when(ordersService.createOrder(any(OrderCreateRequestDto.class), any(Long.class))).thenReturn(responseDto);

        mockMvc.perform(post("/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .session(mockSession))
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(document("orders/create-order",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("orderItems").description("주문 상품 목록"),
                                fieldWithPath("orderItems[].itemId").description("주문 상품 아이디"),
                                fieldWithPath("orderItems[].price").description("주문 상품 단가"),
                                fieldWithPath("orderItems[].quantity").description("주문 상품 수량"),
                                fieldWithPath("couponId").description("사용 된 쿠폰 아이디").optional(),
                                fieldWithPath("zipCode").description("배송지 우편번호"),
                                fieldWithPath("address").description("배송지 주소"),
                                fieldWithPath("packingType").description("포장 타입").type(JsonFieldType.STRING),
                                fieldWithPath("deliveryFee").description("배송비")
                        ),
                        responseFields(
                                fieldWithPath("status").ignored(),
                                fieldWithPath("timestamp").ignored(),
                                fieldWithPath("message").ignored(),
                                fieldWithPath("data").ignored(),
                                fieldWithPath("data.id").description("주문 아이디").type(JsonFieldType.NUMBER),
                                fieldWithPath("data.orderCode").description("주문 번호 ( 비지니스_키 ) "),
                                fieldWithPath("data.orderStatus").description("주문 상태"),
                                fieldWithPath("data.totalAmount").description("총 결제 금액"),
                                fieldWithPath("data.deliveryFee").description("배송비"),
                                fieldWithPath("data.zipCode").description("배송지 우편번호"),
                                fieldWithPath("data.address").description("배송지 주소"),
                                fieldWithPath("data.orderDate").description("구매 일시")
                        )
                ));
    }

    @Test
    @DisplayName("주문 조회")
    void selectOrder() throws Exception {

        List<OrderItemDto> orderItemDtoList = asList(OrderItemDto.builder()
                        .id(1L)
                        .name("name")
                        .quantity(1)
                        .price(1L)
                        .build(),
                OrderItemDto.builder()
                        .id(1L)
                        .name("name")
                        .quantity(1)
                        .price(1L)
                        .build());

        OrderSelectResponseDto responseDto = OrderSelectResponseDto.builder()
                .orderCode("orderCode")
                .userId(1L)
                .userName("userName")
                .status(OrderStatus.PAID)
                .totalAmount(150_000L)
                .deliveryFee(3_000L)
                .orderItems(orderItemDtoList)
                .build();


        mockMvc.perform(get("/v1/orders/{id}", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("orders/select-order",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("주문 아이디")
                        ),
                        responseFields(
                                fieldWithPath("status").ignored(),
                                fieldWithPath("timestamp").ignored(),
                                fieldWithPath("message").ignored(),
                                fieldWithPath("data").ignored(),
                                fieldWithPath("data.orderCode").description("주문 아이디"),
                                fieldWithPath("data.userId").description("주문 고객"),
                                fieldWithPath("data.userName").description("주문 고객명"),
                                fieldWithPath("data.status").description("주문 상태"),
                                fieldWithPath("data.totalAmount").description("총 구매 금액"),
                                fieldWithPath("data.deliveryFee").description("배송비"),
                                fieldWithPath("data.orderItems").description("주문 상품 목록"),
                                fieldWithPath("data.orderItems[].id").description("상품 아이디"),
                                fieldWithPath("data.orderItems[].name").description("상품명"),
                                fieldWithPath("data.orderItems[].quantity").description("상품 수량"),
                                fieldWithPath("data.orderItems[].price").description("상품 단가")
                        )
                ));
    }

    @Test
    @DisplayName("주문 총 금액 계산")
    void calculateTotalPrice() throws Exception {
        OrderCalculateTotalPriceRequestDto requestDto = OrderCalculateTotalPriceRequestDto.builder()
                .orderItems(createOrderItemsByTotalPrice())
                .build();

        mockMvc.perform(get("/v1/orders/total")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("orders/select-total-price",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("orderItems").description("주문 상품 목록"),
                                fieldWithPath("orderItems[].itemId").description("주문 상품 아이디"),
                                fieldWithPath("orderItems[].quantity").description("주문 상품 수량")
                        ),
                        responseFields(
                                fieldWithPath("status").ignored(),
                                fieldWithPath("timestamp").ignored(),
                                fieldWithPath("message").ignored(),
                                fieldWithPath("data").description("총 구매 금액").type(JsonFieldType.NUMBER)
                        )
                ));
    }

    @Test
    @DisplayName("결제 금액 계산")
    void calculatePaymentPrice() throws Exception {
        Long id = 1L;

        mockMvc.perform(get("/v1/orders/{id}/amount", id))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("orders/select-payment-price",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("생성된 주문 아이디")
                        ),
                        responseFields(
                                fieldWithPath("status").ignored(),
                                fieldWithPath("timestamp").ignored(),
                                fieldWithPath("message").ignored(),
                                fieldWithPath("data").description("총 결제 금액").type(JsonFieldType.NUMBER)
                        )
                ));
    }
}

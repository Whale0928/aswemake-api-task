package com.aswemake.api.aswemakeapitask.controller;

import com.aswemake.api.aswemakeapitask.domain.orders.OrderStatus;
import com.aswemake.api.aswemakeapitask.domain.orders.PackingType;
import com.aswemake.api.aswemakeapitask.domain.user.UserRole;
import com.aswemake.api.aswemakeapitask.dto.orders.request.OrderCalculateTotalPriceRequestDto;
import com.aswemake.api.aswemakeapitask.dto.orders.request.OrderCreateRequestDto;
import com.aswemake.api.aswemakeapitask.dto.orders.request.OrderItemRequest;
import com.aswemake.api.aswemakeapitask.dto.orders.response.OrderCreateResponseDto;
import com.aswemake.api.aswemakeapitask.dto.orders.response.OrderItemDto;
import com.aswemake.api.aswemakeapitask.dto.orders.response.OrderSelectResponseDto;
import com.aswemake.api.aswemakeapitask.dto.users.response.UserLoginInfo;
import com.aswemake.api.aswemakeapitask.exception.CustomException;
import com.aswemake.api.aswemakeapitask.exception.ErrorMessages;
import com.aswemake.api.aswemakeapitask.restDocs.RestDocsSupport;
import com.aswemake.api.aswemakeapitask.service.OrdersService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;

import java.util.List;

import static com.aswemake.api.aswemakeapitask.exception.ErrorMessages.USER_NOT_FOUND;
import static java.time.LocalDateTime.now;
import static java.util.Arrays.asList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class OrdersControllerTest extends RestDocsSupport {
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_NAME = "Test User";
    private static final String TEST_SESSION_ID = "sessionId-test-1234";

    @InjectMocks
    private OrdersController ordersController;

    @Mock
    private OrdersService ordersService;

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

    private List<OrderItemRequest> createOrderItems() {
        return List.of(
                OrderItemRequest.of(1L, 10_000L, 5),  // 1번 상품의 단가가 10,000원이라 가정
                OrderItemRequest.of(2L, 15_000L, 10),  // 2번 상품의 단가가 15,000원이라 가정
                OrderItemRequest.of(3L, 20_000L, 2)  // 3번 상품의 단가가 20,000원이라 가정
        );
    }

    @Test
    @DisplayName("주문 생성 할 수 있다")
    void createOrder() throws Exception {
        UserLoginInfo userInfo = createTestUserInfo();
        List<OrderItemRequest> orderItems = createOrderItems();
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
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("로그인하지 않을 경우 예외가 발생한다. ")
    void createOrder_Fail_NotLogin() throws Exception {
        List<OrderItemRequest> orderItems = createOrderItems();
        OrderCreateRequestDto requestDto = OrderCreateRequestDto.builder()
                .orderItems(orderItems)
                .couponId(1L)
                .zipCode("12345")
                .address("서울시 강남구 장난동 101-12")
                .packingType(PackingType.Q_BAG)
                .deliveryFee(5000L)
                .build();

        UserLoginInfo userInfo = UserLoginInfo.builder()
                .sessionId("test-session-id")
                .email(TEST_EMAIL)
                .build();

        MockHttpSession mockSession = new MockHttpSession();
        mockSession.setAttribute("userInfo", userInfo);


        mockMvc.perform(post("/v1/orders")
                        .session(mockSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("마켓은 주문할 수 없다. ")
    void createOrder_Fail_Role_MARKET() throws Exception {
        List<OrderItemRequest> orderItems = createOrderItems();
        OrderCreateRequestDto requestDto = OrderCreateRequestDto.builder()
                .orderItems(orderItems)
                .couponId(1L)
                .zipCode("12345")
                .address("서울시 강남구 장난동 101-12")
                .packingType(PackingType.Q_BAG)
                .deliveryFee(5000L)
                .build();

        UserLoginInfo userInfo = UserLoginInfo.builder()
                .id(1L)
                .sessionId("test-session-id")
                .email(TEST_EMAIL)
                .role(UserRole.MARKET)
                .build();

        MockHttpSession mockSession = new MockHttpSession();
        mockSession.setAttribute("userInfo", userInfo);


        mockMvc.perform(post("/v1/orders")
                        .session(mockSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("사용자가 없을 경우 예외가 발생한다. ")
    void createOrder_Fail_UserNotFound() throws Exception {
        List<OrderItemRequest> orderItems = createOrderItems();
        OrderCreateRequestDto requestDto = OrderCreateRequestDto.builder()
                .orderItems(orderItems)
                .couponId(1L)
                .zipCode("12345")
                .address("서울시 강남구 장난동 101-12")
                .packingType(PackingType.Q_BAG)
                .deliveryFee(5000L)
                .build();

        UserLoginInfo userInfo = UserLoginInfo.builder()
                .id(1L)
                .sessionId("test-session-id")
                .email(TEST_EMAIL)
                .role(UserRole.USER)
                .build();

        MockHttpSession mockSession = new MockHttpSession();
        mockSession.setAttribute("userInfo", userInfo);

        when(ordersService.createOrder(any(OrderCreateRequestDto.class), any(Long.class)))
                .thenThrow(new CustomException(HttpStatus.NOT_FOUND, USER_NOT_FOUND));

        mockMvc.perform(post("/v1/orders")
                        .session(mockSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("주문 생성시 재고가 부족할 경우 예외가 발생한다. ")
    void createOrder_Fail_StockNotEnough() throws Exception {
        List<OrderItemRequest> orderItems = createOrderItems();
        OrderCreateRequestDto requestDto = OrderCreateRequestDto.builder()
                .orderItems(orderItems)
                .couponId(1L)
                .zipCode("12345")
                .address("서울시 강남구 장난동 101-12")
                .packingType(PackingType.Q_BAG)
                .deliveryFee(5000L)
                .build();

        UserLoginInfo userInfo = UserLoginInfo.builder()
                .id(1L)
                .sessionId("test-session-id")
                .email(TEST_EMAIL)
                .role(UserRole.USER)
                .build();

        MockHttpSession mockSession = new MockHttpSession();
        mockSession.setAttribute("userInfo", userInfo);

        when(ordersService.createOrder(any(OrderCreateRequestDto.class), any(Long.class)))
                .thenThrow(new CustomException(HttpStatus.BAD_REQUEST, ErrorMessages.ITEM_STOCK_NOT_ENOUGH));

        mockMvc.perform(post("/v1/orders")
                        .session(mockSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("주문 생성시 유효한 쿠폰이 아닐 경우 경우 예외가 발생한다. ")
    void createOrder_Fail_CouponNotFound() throws Exception {
        List<OrderItemRequest> orderItems = createOrderItems();
        OrderCreateRequestDto requestDto = OrderCreateRequestDto.builder()
                .orderItems(orderItems)
                .couponId(1L)
                .zipCode("12345")
                .address("서울시 강남구 장난동 101-12")
                .packingType(PackingType.Q_BAG)
                .deliveryFee(5000L)
                .build();

        UserLoginInfo userInfo = UserLoginInfo.builder()
                .id(1L)
                .sessionId("test-session-id")
                .email(TEST_EMAIL)
                .role(UserRole.USER)
                .build();

        MockHttpSession mockSession = new MockHttpSession();
        mockSession.setAttribute("userInfo", userInfo);

        when(ordersService.createOrder(any(OrderCreateRequestDto.class), any(Long.class)))
                .thenThrow(new CustomException(HttpStatus.BAD_REQUEST, ErrorMessages.COUPON_NOT_FOUND));

        mockMvc.perform(post("/v1/orders")
                        .session(mockSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("주문을 조회할 수 있다.")
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

        when(ordersService.selectOrder(any(Long.class))).thenReturn(responseDto);
        mockMvc.perform(get("/v1/orders/{id}", 1L))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("잘못된 주문번호로 조회 시 예외가 발생한다.")
    void selectOrder_fail_invalidOrderCode() throws Exception {
        when(ordersService.selectOrder(any(Long.class)))
                .thenThrow(new CustomException(HttpStatus.BAD_REQUEST, ErrorMessages.ORDER_NOT_FOUND));

        mockMvc.perform(get("/v1/orders/{id}", 1L))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("주문 총 금액 계산")
    void calculateTotalPrice() throws Exception {
        OrderCalculateTotalPriceRequestDto requestDto = OrderCalculateTotalPriceRequestDto.builder()
                .orderItems(createOrderItems())
                .deliveryFee(3000L)
                .build();

        mockMvc.perform(get("/v1/orders/total")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("결제 금액 계산")
    void calculatePaymentPrice() throws Exception {
        Long id = 1L;

        mockMvc.perform(get("/v1/orders/{id}/amount", id))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("존재하지 않는 결제번호로 결제금액을 계산 시 예외가 발생한다. ")
    void calculatePaymentPrice_fail_invalidOrderCode() throws Exception {
        when(ordersService.calculatePaymentPrice(any(Long.class)))
                .thenThrow(new CustomException(HttpStatus.BAD_REQUEST, ErrorMessages.ORDER_NOT_FOUND));

        mockMvc.perform(get("/v1/orders/{id}/amount", 1L))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}
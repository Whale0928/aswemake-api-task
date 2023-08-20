package com.aswemake.api.aswemakeapitask.service;

import com.aswemake.api.aswemakeapitask.domain.item.Item;
import com.aswemake.api.aswemakeapitask.domain.orders.OrderItem;
import com.aswemake.api.aswemakeapitask.domain.orders.Orders;
import com.aswemake.api.aswemakeapitask.domain.orders.OrdersRepository;
import com.aswemake.api.aswemakeapitask.domain.user.UserRepository;
import com.aswemake.api.aswemakeapitask.domain.user.Users;
import com.aswemake.api.aswemakeapitask.dto.orders.request.OrderCalculateTotalPriceRequestDto;
import com.aswemake.api.aswemakeapitask.dto.orders.request.OrderCreateRequestDto;
import com.aswemake.api.aswemakeapitask.dto.orders.request.OrderItemRequest;
import com.aswemake.api.aswemakeapitask.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;


@ExtendWith(MockitoExtension.class)
class OrdersServiceTest {

    @InjectMocks
    private OrdersService ordersService;

    @Mock
    private OrdersRepository ordersRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CouponCalculate couponCalculate;

    @Mock
    private ItemStockService itemStockService;

    private Orders order;
    private Users user;

    @BeforeEach
    void setUp() {
        user = Users.builder()
                .name("Test User")
                .build();
        setField(user, "id", 1L);

        order = Orders.builder()
                .orderCode("TEST-ORDER-CODE")
                .users(user)
                .build();
        setField(order, "id", 1L);
    }

    @Test
    @DisplayName("주문 생성 할 수 있다.")
    void createOrder_Success() throws Exception {
        OrderCreateRequestDto requestDto = new OrderCreateRequestDto();
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(ordersRepository.saveAndFlush(any())).thenReturn(order);
        assertNotNull(ordersService.createOrder(requestDto, 1L));
    }

    @Test
    @DisplayName("주문자가 존재하지 않을 때 예외 발생한다")
    void createOrder_UserNotFound() {
        OrderCreateRequestDto requestDto = new OrderCreateRequestDto();
        when(userRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(CustomException.class, () -> ordersService.createOrder(requestDto, 1L));
    }

    @Test
    @DisplayName("주문을 조회 할 수 있다.")
    void selectOrder_Success() {
        when(ordersRepository.findByIdForSelectOrder(any())).thenReturn(Optional.of(order));
        assertNotNull(ordersService.selectOrder(1L));
    }

    @Test
    @DisplayName("주문이 존재하지 않을 때 예외 발생한다")
    void selectOrder_NotFound() {
        when(ordersRepository.findByIdForSelectOrder(any())).thenReturn(Optional.empty());
        assertThrows(CustomException.class, () -> ordersService.selectOrder(1L));
    }


    @Test
    @DisplayName("총 금액 계산 성공")
    void calculateTotalPrice_Success() {
        OrderItemRequest orderItem1 = OrderItemRequest.of(1L, 1000L, 2);
        OrderItemRequest orderItem2 = OrderItemRequest.of(2L, 2000L, 1);
        OrderCalculateTotalPriceRequestDto requestDto = OrderCalculateTotalPriceRequestDto.builder()
                .orderItems(Arrays.asList(orderItem1, orderItem2))
                .deliveryFee(5000L)
                .build();

        Long expectedTotal = 5000L + (1000L * 2) + (2000L * 1);
        Long actualTotal = ordersService.calculateTotalPrice(requestDto);

        assertEquals(expectedTotal, actualTotal);
    }

    @Test
    @DisplayName("결제 금액 계산을 할 수 있다.")
    void calculatePaymentPrice_Success() {
        Item mockItem = Item.builder().price(1000L).build();

        OrderItem mockOrderItem = OrderItem.builder()
                .item(mockItem)
                .quantity(3)
                .build();

        setField(order, "orderItems", Collections.singletonList(mockOrderItem));
        when(ordersRepository.findByIdForSelectOrder(1L)).thenReturn(Optional.of(order));

        Long expectedPayment = 3000L;
        Long actualPayment = ordersService.calculatePaymentPrice(1L);

        assertEquals(expectedPayment, actualPayment);
    }

    @Test
    @DisplayName("주문이 존재하지 않을 때 결제 금액 계산 실패")
    void calculatePaymentPrice_OrderNotFound() {
        when(ordersRepository.findByIdForSelectOrder(1L)).thenReturn(Optional.empty());
        assertThrows(CustomException.class, () -> ordersService.calculatePaymentPrice(1L));
    }
}
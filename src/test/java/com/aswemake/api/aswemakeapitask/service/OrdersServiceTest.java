package com.aswemake.api.aswemakeapitask.service;

import com.aswemake.api.aswemakeapitask.domain.coupon.CouponRepository;
import com.aswemake.api.aswemakeapitask.domain.orders.OrdersRepository;
import com.aswemake.api.aswemakeapitask.domain.user.UserRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrdersServiceTest {

    @InjectMocks
    private OrdersService ordersService;
    @Mock
    private OrdersRepository ordersRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CouponRepository couponRepository;



}
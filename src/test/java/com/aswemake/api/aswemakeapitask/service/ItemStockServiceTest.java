package com.aswemake.api.aswemakeapitask.service;

import com.aswemake.api.aswemakeapitask.domain.item.Item;
import com.aswemake.api.aswemakeapitask.domain.item.ItemRepository;
import com.aswemake.api.aswemakeapitask.dto.orders.request.OrderItemRequest;
import com.aswemake.api.aswemakeapitask.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@ExtendWith(MockitoExtension.class)
class ItemStockServiceTest {

    @InjectMocks
    private ItemStockService itemStockService;

    @Mock
    private ItemRepository itemRepository;

    private Item item;

    @BeforeEach
    void setUp() {
        item = Item.builder()
                .name("Test Item")
                .price(1000L)
                .stockQuantity(10)
                .remainingStockQuantity(10)
                .build();
        setField(item, "id", 1L);
    }

    @Test
    @DisplayName("재고 감소 성공")
    void decreaseStock_Success() {
        when(itemRepository.findByIdForUpdate(anyLong())).thenReturn(Optional.of(item));

        OrderItemRequest orderItemRequest = OrderItemRequest.of(item.getId(), 5000L, 5);
        itemStockService.decreaseStock(Collections.singletonList(orderItemRequest));
    }

    @Test
    @DisplayName("재고 부족 예외 발생")
    void decreaseStock_NotEnoughStock() {
        when(itemRepository.findByIdForUpdate(anyLong())).thenReturn(Optional.of(item));
        OrderItemRequest orderItemRequest = OrderItemRequest.of(item.getId(), 5000L, 100000);
        assertThrows(CustomException.class, () -> itemStockService.decreaseStock(Collections.singletonList(orderItemRequest)));
    }

    @Test
    @DisplayName("상품이 존재하지 않을 때 예외 발생")
    void decreaseStock_ItemNotFound() {
        when(itemRepository.findByIdForUpdate(anyLong())).thenReturn(Optional.empty());

        OrderItemRequest orderItemRequest = OrderItemRequest.of(item.getId(), 5000L, 5);
        assertThrows(CustomException.class, () -> itemStockService.decreaseStock(Collections.singletonList(orderItemRequest)));
    }
}

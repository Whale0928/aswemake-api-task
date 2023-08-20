package com.aswemake.api.aswemakeapitask.service;

import com.aswemake.api.aswemakeapitask.domain.item.Item;
import com.aswemake.api.aswemakeapitask.domain.item.ItemHistoryRepository;
import com.aswemake.api.aswemakeapitask.domain.item.ItemRepository;
import com.aswemake.api.aswemakeapitask.domain.item.PriceChangeStatus;
import com.aswemake.api.aswemakeapitask.domain.item.PriceHistory;
import com.aswemake.api.aswemakeapitask.domain.orders.OrderItem;
import com.aswemake.api.aswemakeapitask.dto.item.request.ItemCreateRequestDto;
import com.aswemake.api.aswemakeapitask.dto.item.request.ItemUpdateRequestDto;
import com.aswemake.api.aswemakeapitask.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;


@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @InjectMocks
    private ItemService itemService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemHistoryRepository itemHistoryRepository;

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
    @DisplayName("상품 정보를 조회할 수 있다.")
    void selectItem_Success() throws Exception {
        when(itemRepository.findByIdWithOrderItem(anyLong())).thenReturn(Optional.of(item));

        // When
        var response = itemService.selectItem(1L);

        // Then
        assertThat(response.getName()).isEqualTo(item.getName());
        assertThat(response.getPrice()).isEqualTo(item.getPrice());
    }

    @Test
    @DisplayName("존재하지 않는 상품 조회 시 예외 발생한다.")
    void selectItem_NotFound() {
        when(itemRepository.findByIdWithOrderItem(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(Exception.class, () -> itemService.selectItem(1L));
    }

    @Test
    @DisplayName("상품을 생성 할 수 있다.")
    void createItem_Success() throws Exception {
        when(itemRepository.findByName(anyString())).thenReturn(Optional.empty());
        when(itemRepository.saveAndFlush(any())).thenReturn(item);

        // When
        var request = ItemCreateRequestDto.builder()
                .name("Test Item")
                .price(1000L)
                .stockQuantity(10)
                .build();
        var response = itemService.createItem(request);

        // Then
        assertThat(response.getName()).isEqualTo(item.getName());
        assertThat(response.getPrice()).isEqualTo(item.getPrice());
    }

    @Test
    @DisplayName("아이템 이름 중복 시 예외 발생한다")
    void createItem_NameDuplication() {
        when(itemRepository.findByName(anyString())).thenReturn(Optional.of(item));

        // When & Then
        var request = ItemCreateRequestDto.builder()
                .name("Test Item")
                .price(1000L)
                .stockQuantity(10)
                .build();
        assertThrows(CustomException.class, () -> itemService.createItem(request));
    }

    @Test
    @DisplayName("상품 가격을 업데이트 할 수 있다.")
    void updateItem_Success() throws Exception {
        when(itemRepository.findByIdWithOrderItem(anyLong())).thenReturn(Optional.of(item));
        when(itemRepository.saveAndFlush(any())).thenReturn(item);
        when(itemHistoryRepository.saveAndFlush(any())).thenReturn(null);

        // When
        var request = ItemUpdateRequestDto.builder().price(1500L).build();
        var response = itemService.updateItem(1L, request);

        // Then
        assertThat(response.getName()).isEqualTo(item.getName());
        assertThat(response.getAfterPrice()).isEqualTo(1500L);
    }

    @Test
    @DisplayName("상품 가격 업데이트 시 가격이 동일하면 예외 발생한다.")
    void updateItem_PriceNotChanged() throws Exception {
        when(itemRepository.findByIdWithOrderItem(anyLong())).thenReturn(Optional.of(item));

        // When & Then
        var request = ItemUpdateRequestDto.builder().price(1000L).build();
        assertThrows(CustomException.class, () -> itemService.updateItem(1L, request));
    }

    @Test
    @DisplayName("상품을 삭제 할 수 있다.")
    void deleteItem_Success() throws Exception {
        when(itemRepository.findByIdWithOrderItem(anyLong())).thenReturn(Optional.of(item));
        doNothing().when(itemRepository).delete(any()); // 이 부분을 추가합니다.

        // When
        var response = itemService.deleteItem(1L);
        // Then
        assertThat(response.getName()).isEqualTo(item.getName());
        assertThat(response.getRemainingStockQuantity()).isEqualTo(item.getRemainingStockQuantity());
    }

    @Test
    @DisplayName("상품 삭제 시 주문 항목이 있으면 예외 발생한다.")
    void deleteItem_OrderItemsExist() throws Exception {
        Item itemWithOrderItems = Item.builder()
                .name("Test Item with Orders")
                .price(1000L)
                .stockQuantity(10)
                .remainingStockQuantity(10)
                .build();

        // 주문 항목 생성
        OrderItem orderItem = OrderItem.builder()
                .item(itemWithOrderItems)
                .quantity(5)
                .build();

        // 주문 항목을 상품에 연결
        List<OrderItem> orderItems = new ArrayList<>();
        orderItems.add(orderItem);
        // 상품에 주문 항목을 연결 리플렉션을 활용
        ReflectionTestUtils.setField(itemWithOrderItems, "orderItems", orderItems);

        when(itemRepository.findByIdWithOrderItem(anyLong())).thenReturn(Optional.of(itemWithOrderItems));

        // When & Then
        assertThrows(CustomException.class, () -> itemService.deleteItem(1L));
    }

    @Test
    @DisplayName("특정 시간에 대한 상품 가격을 조회할 수 있다.")
    void selectItemPriceAtTime_Success() throws Exception {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemHistoryRepository.findTopByItemIdAndChangedDateBeforeOrderByChangedDateDesc(anyLong(), any())).thenReturn(Optional.empty());

        // When
        var response = itemService.selectItemPriceAtTime(1L, "2023-01-20T23:57");

        // Then
        assertThat(response.getName()).isEqualTo(item.getName());
        assertThat(response.getPrice()).isEqualTo(item.getPrice());
    }

    @Test
    @DisplayName("특정 시간에 대한 상품 가격을 조회할 수 있다. (가격 변경 이력 없음)")
    void selectItemPriceAtTime_NoHistory() throws Exception {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemHistoryRepository.findTopByItemIdAndChangedDateBeforeOrderByChangedDateDesc(anyLong(), any())).thenReturn(Optional.empty());

        var response = itemService.selectItemPriceAtTime(1L, "2023-01-20T23:57");

        assertThat(response.getName()).isEqualTo(item.getName());
        assertThat(response.getPrice()).isEqualTo(item.getPrice());
    }

    @Test
    @DisplayName("특정 시간에 대한 상품 가격을 조회할 수 있다. (가격 변경 이력 있음)")
    void selectItemPriceAtTime_WithHistory() throws Exception {
        PriceHistory priceHistory = PriceHistory.builder()
                .item(item)
                .price(1500L)
                .priceChangeStatus(PriceChangeStatus.INCREASED)
                .changedDate(LocalDateTime.parse("2023-01-19T23:57"))
                .build();

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemHistoryRepository.findTopByItemIdAndChangedDateBeforeOrderByChangedDateDesc(anyLong(), any())).thenReturn(Optional.of(priceHistory));

        var response = itemService.selectItemPriceAtTime(1L, "2023-01-20T23:57");

        assertThat(response.getName()).isEqualTo(item.getName());
        assertThat(response.getPrice()).isEqualTo(priceHistory.getPrice());
    }

    @Test
    @DisplayName("존재하지 않는 상품의 가격을 조회하려 할 때 예외 발생한다.")
    void selectItemPriceAtTime_ItemNotFound() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(Exception.class, () -> itemService.selectItemPriceAtTime(1L, "2023-01-20T23:57"));
    }
}

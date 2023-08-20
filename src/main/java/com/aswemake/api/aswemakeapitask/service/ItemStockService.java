package com.aswemake.api.aswemakeapitask.service;

import com.aswemake.api.aswemakeapitask.domain.item.Item;
import com.aswemake.api.aswemakeapitask.domain.item.ItemHistoryRepository;
import com.aswemake.api.aswemakeapitask.domain.item.ItemRepository;
import com.aswemake.api.aswemakeapitask.dto.orders.request.OrderItemRequest;
import com.aswemake.api.aswemakeapitask.exception.CustomException;
import com.aswemake.api.aswemakeapitask.exception.ErrorMessages;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemStockService {
    private final ItemRepository itemRepository;
    private final ItemHistoryRepository itemHistoryRepository;

    @Transactional
    public void decreaseStock(List<OrderItemRequest> orderItems) {
        for (OrderItemRequest orderItem : orderItems) {

            // 상품 조회 with 락
            Item item = itemRepository.findByIdForUpdate(orderItem.getItemId())
                    .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, ErrorMessages.ITEM_NOT_FOUND));

            // 재고 현 재고 확인 및 감소
            if (item.getRemainingStockQuantity() < orderItem.getQuantity())
                throw new CustomException(HttpStatus.NOT_FOUND, ErrorMessages.ITEM_STOCK_NOT_ENOUGH);

            item.decreaseStock(orderItem.getQuantity());
        }
    }
}
package com.aswemake.api.aswemakeapitask.service;

import com.aswemake.api.aswemakeapitask.domain.item.Item;
import com.aswemake.api.aswemakeapitask.domain.item.ItemHistoryRepository;
import com.aswemake.api.aswemakeapitask.domain.item.ItemRepository;
import com.aswemake.api.aswemakeapitask.domain.orders.OrderItem;
import com.aswemake.api.aswemakeapitask.dto.item.request.ItemCreateRequestDto;
import com.aswemake.api.aswemakeapitask.dto.item.response.ItemCreateResponseDto;
import com.aswemake.api.aswemakeapitask.dto.item.response.ItemSelectResponseDto;
import com.aswemake.api.aswemakeapitask.exception.CustomException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.aswemake.api.aswemakeapitask.exception.ErrorMessages.ITEM_NAME_DUPLICATION;
import static com.aswemake.api.aswemakeapitask.exception.ErrorMessages.ITEM_NOT_FOUND;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final ItemHistoryRepository itemHistoryRepository;

    public ItemSelectResponseDto selectItem(Long id) throws Exception {
        Item item = itemRepository.findByIdWithOrderItem(id)
                .orElseThrow(() -> new Exception(new CustomException(NOT_FOUND, ITEM_NOT_FOUND)));
        int totalCount = item.getOrderItems().stream().mapToInt(OrderItem::getQuantity).sum();
        return ItemSelectResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .price(item.getPrice())
                .stockQuantity(item.getStockQuantity())
                .remainingStockQuantity(item.getStockQuantity() - totalCount)
                .build();
    }

    @Transactional
    public ItemCreateResponseDto createItem(ItemCreateRequestDto request) throws Exception {

        itemRepository.findByName(request.getName())
                .ifPresent(item -> {
                    throw new CustomException(BAD_REQUEST, ITEM_NAME_DUPLICATION);
                });

        Item item = itemRepository.saveAndFlush(Item.builder()
                .name(request.getName())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .build());

        return ItemCreateResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .price(item.getPrice())
                .stockQuantity(item.getStockQuantity())
                .build();
    }
}

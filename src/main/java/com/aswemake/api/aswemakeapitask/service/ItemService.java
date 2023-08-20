package com.aswemake.api.aswemakeapitask.service;

import com.aswemake.api.aswemakeapitask.common.util.DateUtils;
import com.aswemake.api.aswemakeapitask.domain.item.Item;
import com.aswemake.api.aswemakeapitask.domain.item.ItemHistoryRepository;
import com.aswemake.api.aswemakeapitask.domain.item.ItemRepository;
import com.aswemake.api.aswemakeapitask.domain.item.PriceHistory;
import com.aswemake.api.aswemakeapitask.dto.item.request.ItemCreateRequestDto;
import com.aswemake.api.aswemakeapitask.dto.item.request.ItemUpdateRequestDto;
import com.aswemake.api.aswemakeapitask.dto.item.response.ItemCreateResponseDto;
import com.aswemake.api.aswemakeapitask.dto.item.response.ItemDeleteResponseDto;
import com.aswemake.api.aswemakeapitask.dto.item.response.ItemPriceAtTimeResponseDto;
import com.aswemake.api.aswemakeapitask.dto.item.response.ItemSelectResponseDto;
import com.aswemake.api.aswemakeapitask.dto.item.response.ItemUpdateResponseDto;
import com.aswemake.api.aswemakeapitask.exception.CustomException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.aswemake.api.aswemakeapitask.domain.item.PriceChangeStatus.DECREASED;
import static com.aswemake.api.aswemakeapitask.domain.item.PriceChangeStatus.INCREASED;
import static com.aswemake.api.aswemakeapitask.exception.ErrorMessages.ITEM_DELETE_NOT_POSSIBLE;
import static com.aswemake.api.aswemakeapitask.exception.ErrorMessages.ITEM_NAME_DUPLICATION;
import static com.aswemake.api.aswemakeapitask.exception.ErrorMessages.ITEM_NOT_FOUND;
import static com.aswemake.api.aswemakeapitask.exception.ErrorMessages.ITEM_PRICE_NOT_CHANGED;
import static java.time.LocalDateTime.now;
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

        return ItemSelectResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .price(item.getPrice())
                .stockQuantity(item.getStockQuantity())
                .remainingStockQuantity(item.getRemainingStockQuantity())
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
                .remainingStockQuantity(request.getStockQuantity())
                .build());

        return ItemCreateResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .price(item.getPrice())
                .stockQuantity(item.getStockQuantity())
                .build();
    }

    @Transactional
    public ItemUpdateResponseDto updateItem(Long id, ItemUpdateRequestDto request) throws Exception {
        Item item = itemRepository.findByIdWithOrderItem(id)
                .orElseThrow(() -> new Exception(new CustomException(NOT_FOUND, ITEM_NOT_FOUND)));

        Long beforePrice = item.getPrice();

        if (request.getPrice().equals(beforePrice)) {
            throw new CustomException(BAD_REQUEST, ITEM_PRICE_NOT_CHANGED);
        }

        item.updatePrice(request.getPrice());
        itemRepository.saveAndFlush(item);

        PriceHistory priceHistory = PriceHistory.builder()
                .item(item)
                .price(item.getPrice())
                .priceChangeStatus(beforePrice < request.getPrice() ? INCREASED : DECREASED)
                .changedDate(now())
                .build();

        itemHistoryRepository.saveAndFlush(priceHistory);

        return ItemUpdateResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .beforePrice(beforePrice)
                .afterPrice(request.getPrice())
                .stockQuantity(item.getStockQuantity())
                .remainingStockQuantity(item.getRemainingStockQuantity())
                .build();
    }

    @Transactional
    public ItemDeleteResponseDto deleteItem(Long id) throws Exception {
        Item item = itemRepository.findByIdWithOrderItem(id)
                .orElseThrow(() -> new Exception(new CustomException(NOT_FOUND, ITEM_NOT_FOUND)));

        String deletedItemName = item.getName();
        int deleteAtQuantity = item.getRemainingStockQuantity();

        if (!item.getOrderItems().isEmpty()) {
            throw new CustomException(BAD_REQUEST, ITEM_DELETE_NOT_POSSIBLE);
        }

        itemRepository.delete(item);

        return ItemDeleteResponseDto.builder()
                .id(id)
                .name(deletedItemName)
                .remainingStockQuantity(deleteAtQuantity)
                .deletedAt(now())
                .build();
    }

    public ItemPriceAtTimeResponseDto selectItemPriceAtTime(Long id, String date) throws Exception {
        LocalDateTime dataAt = DateUtils.parseDate(date);

        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new Exception(new CustomException(NOT_FOUND, ITEM_NOT_FOUND)));

        // 특정 시점 기준 조회
        Optional<PriceHistory> optionalPriceHistory = itemHistoryRepository.findTopByItemIdAndChangedDateBeforeOrderByChangedDateDesc(id, dataAt);
        Long priceAtGivenDate = optionalPriceHistory.map(PriceHistory::getPrice).orElse(item.getPrice());

        return ItemPriceAtTimeResponseDto.builder()
                .id(id)
                .name(item.getName())
                .date(String.valueOf(dataAt))
                .price(priceAtGivenDate)
                .currentPrice(item.getPrice())
                .build();
    }
}

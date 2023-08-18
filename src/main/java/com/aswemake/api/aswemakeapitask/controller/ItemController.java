package com.aswemake.api.aswemakeapitask.controller;


import com.aswemake.api.aswemakeapitask.dto.GlobalResponse;
import com.aswemake.api.aswemakeapitask.dto.item.request.ItemCreateRequestDto;
import com.aswemake.api.aswemakeapitask.dto.item.request.ItemUpdateRequestDto;
import com.aswemake.api.aswemakeapitask.dto.item.response.ItemDeleteResponseDto;
import com.aswemake.api.aswemakeapitask.dto.item.response.ItemPriceAtTimeResponseDto;
import com.aswemake.api.aswemakeapitask.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.ok;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/items")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/{id}")
    public ResponseEntity<GlobalResponse> selectItem(@PathVariable Long id) throws Exception {
        return GlobalResponse.ok(itemService.selectItem(id));
    }

    @PostMapping
    public ResponseEntity<GlobalResponse> createItem(@Valid @RequestBody ItemCreateRequestDto request) throws Exception {
        return GlobalResponse.created(itemService.createItem(request));
    }
    @PutMapping("{id}")
    public ResponseEntity<GlobalResponse> updateItem(@PathVariable Long id, @Valid @RequestBody ItemUpdateRequestDto request) throws Exception {
        return GlobalResponse.ok(itemService.updateItem(id, request));
    }

    // TODO : DELETE /items/{id}: 상품 삭제
    @DeleteMapping("{id}")
    public ResponseEntity<GlobalResponse> deleteItem(@PathVariable Long id) {
        return ok(GlobalResponse.builder()
                .status(HttpStatus.OK)
                .message("삭제 성공")
                .data(ItemDeleteResponseDto.builder()
                        .id(1L)
                        .name("테스트 상품_AAA")
                        .remainingStockQuantity(50)
                        .deletedAt(null)
                        .build())
                .build());
    }

    // TODO : GET /items/{id}/price?date=YYYY-MM-DD: 특정 시점의 상품 가격 조회
    @GetMapping("{id}/price")
    public ResponseEntity<GlobalResponse> selectItemPriceAtTime(@PathVariable Long id, String date) {
        return ok(GlobalResponse.builder()
                .status(HttpStatus.OK)
                .message("조회 성공")
                .data(ItemPriceAtTimeResponseDto.builder()
                        .id(1L)
                        .name("테스트 상품_AAA")
                        .date("2021-10-10")
                        .price(8500)
                        .currentPrice(11000)
                        .build())
                .build());
    }

}

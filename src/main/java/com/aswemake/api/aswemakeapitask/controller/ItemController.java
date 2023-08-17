package com.aswemake.api.aswemakeapitask.controller;


import com.aswemake.api.aswemakeapitask.dto.GlobalResponse;
import com.aswemake.api.aswemakeapitask.dto.item.request.ItemCreateRequestDto;
import com.aswemake.api.aswemakeapitask.dto.item.response.ItemCreateResponseDto;
import com.aswemake.api.aswemakeapitask.dto.item.response.ItemSelectResponseDto;
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


    // TODO : GET /items/{id}: 상품 정보 조회
    @GetMapping("{id}")
    public ResponseEntity<GlobalResponse> selectItem(@PathVariable Long id) {
        return ok(GlobalResponse.builder()
                .status(HttpStatus.OK)
                .message("OK")
                .data(ItemSelectResponseDto.builder()
                        .id(1L)
                        .name("테스트 상품_AAA")
                        .price(1000)
                        .stockQuantity(100)
                        .remainingStockQuantity(50)
                        .build())
                .build());
    }

    // TODO : POST /items: 상품 생성
    @PostMapping
    public ResponseEntity<GlobalResponse> createItem(@Valid @RequestBody ItemCreateRequestDto request) {
        return ok(GlobalResponse.builder()
                .status(HttpStatus.CREATED)
                .message("상품 생성 성공")
                .data(ItemCreateResponseDto.builder()
                        .id(1L)
                        .name("생성된_상품_AAA")
                        .price(1000)
                        .build())
                .build());
    }

    // TODO : PUT /items/{id}: 상품 가격 수정
    @PutMapping("{id}")
    public ResponseEntity<GlobalResponse> updateItem(@PathVariable Long id) {
        return null;
    }

    // TODO : DELETE /items/{id}: 상품 삭제
    @DeleteMapping("{id}")
    public ResponseEntity<GlobalResponse> deleteItem(@PathVariable Long id) {
        return null;
    }

    // TODO : GET /items/{id}/price?date=YYYY-MM-DD: 특정 시점의 상품 가격 조회
    @GetMapping("{id}/price")
    public ResponseEntity<GlobalResponse> selectItemPrice(@PathVariable Long id, String date) {
        return null;
    }

}

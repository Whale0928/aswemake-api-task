package com.aswemake.api.aswemakeapitask.controller;


import com.aswemake.api.aswemakeapitask.dto.GlobalResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/items")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ItemController {


    // TODO : GET /items/{id}: 상품 정보 조회
    @GetMapping("{id}")
    public ResponseEntity<GlobalResponse> selectItem(@PathVariable Long id) {
        return null;
    }

    // TODO : POST /items: 상품 생성
    @PostMapping
    public ResponseEntity<GlobalResponse> createItem() {
        return null;
    }

    // TODO : PUT /items/{id}: 상품 가격 수정
    @PutMapping("{id}")
    public ResponseEntity<GlobalResponse> updateItem(@PathVariable Long id) {
        return null;
    }

    // DELETE /items/{id}: 상품 삭제
    @DeleteMapping("{id}")
    public ResponseEntity<GlobalResponse> deleteItem(@PathVariable Long id) {
        return null;
    }

    // GET /items/{id}/price?date=YYYY-MM-DD: 특정 시점의 상품 가격 조회
    @GetMapping("{id}/price")
    public ResponseEntity<GlobalResponse> selectItemPrice(@PathVariable Long id, String date) {
        return null;
    }

}

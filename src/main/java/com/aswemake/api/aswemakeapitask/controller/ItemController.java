package com.aswemake.api.aswemakeapitask.controller;


import com.aswemake.api.aswemakeapitask.dto.GlobalResponse;
import com.aswemake.api.aswemakeapitask.dto.item.request.ItemCreateRequestDto;
import com.aswemake.api.aswemakeapitask.dto.item.request.ItemUpdateRequestDto;
import com.aswemake.api.aswemakeapitask.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    @PutMapping("/{id}")
    public ResponseEntity<GlobalResponse> updateItem(@PathVariable Long id, @Valid @RequestBody ItemUpdateRequestDto request) throws Exception {
        return GlobalResponse.ok(itemService.updateItem(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GlobalResponse> deleteItem(@PathVariable Long id) throws Exception {
        return GlobalResponse.ok(itemService.deleteItem(id));
    }

    @GetMapping("{id}/price")
    public ResponseEntity<GlobalResponse> selectItemPriceAtTime(@PathVariable Long id, String date) throws Exception {
        return GlobalResponse.ok(itemService.selectItemPriceAtTime(id, date));
    }

}

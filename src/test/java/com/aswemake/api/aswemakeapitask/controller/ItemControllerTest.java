package com.aswemake.api.aswemakeapitask.controller;

import com.aswemake.api.aswemakeapitask.dto.item.request.ItemCreateRequestDto;
import com.aswemake.api.aswemakeapitask.dto.item.request.ItemUpdateRequestDto;
import com.aswemake.api.aswemakeapitask.dto.item.response.ItemCreateResponseDto;
import com.aswemake.api.aswemakeapitask.dto.item.response.ItemDeleteResponseDto;
import com.aswemake.api.aswemakeapitask.dto.item.response.ItemPriceAtTimeResponseDto;
import com.aswemake.api.aswemakeapitask.dto.item.response.ItemSelectResponseDto;
import com.aswemake.api.aswemakeapitask.dto.item.response.ItemUpdateResponseDto;
import com.aswemake.api.aswemakeapitask.exception.CustomException;
import com.aswemake.api.aswemakeapitask.restDocs.RestDocsSupport;
import com.aswemake.api.aswemakeapitask.service.ItemService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.aswemake.api.aswemakeapitask.exception.ErrorMessages.ITEM_DELETE_NOT_POSSIBLE;
import static com.aswemake.api.aswemakeapitask.exception.ErrorMessages.ITEM_NAME_DUPLICATION;
import static com.aswemake.api.aswemakeapitask.exception.ErrorMessages.ITEM_NOT_FOUND;
import static com.aswemake.api.aswemakeapitask.exception.ErrorMessages.ITEM_PRICE_NOT_CHANGED;
import static java.time.LocalDateTime.now;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest extends RestDocsSupport {

    @InjectMocks
    private ItemController itemController;

    @Mock
    private ItemService itemService;

    @Override
    protected Object initController() {
        return new ItemController(itemService);
    }

    @Test
    @DisplayName("유효한 아이템 ID로 아이템 정보 조회 시 아이템 정보를 반환한다.")
    public void selectItem_Valid_Id() throws Exception {
        // Mock the service call to return a valid ItemSelectResponseDto
        ItemSelectResponseDto responseDto = ItemSelectResponseDto.builder()
                .id(1L)
                .name("Sample Item")
                .price(100L)
                .stockQuantity(10)
                .remainingStockQuantity(5)
                .build();

        when(itemService.selectItem(1L)).thenReturn(responseDto);

        mockMvc.perform(get("/v1/items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.name").value("Sample Item"))
                .andExpect(jsonPath("$.data.price").value(100.0))
                .andExpect(jsonPath("$.data.stockQuantity").value(10))
                .andExpect(jsonPath("$.data.remainingStockQuantity").value(5));
    }

    @Test
    @DisplayName("존재하지 않는 아이템 ID로 아이템 정보 조회 시 예외가 발생한다.")
    public void selectItem_Invalid_Id() throws Exception {
        // Mock the service call to throw an exception
        when(itemService.selectItem(999L)).thenThrow(new Exception(new CustomException(NOT_FOUND, ITEM_NOT_FOUND)));


        mockMvc.perform(get("/v1/items/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("잘못된 형식의 아이템 ID로 아이템 정보 조회시 예외가 발생한다.")
    public void selectItem_Invalid_Id_Format() throws Exception {
        String invalidId = "invalidId";
        mockMvc.perform(get("/v1/items/" + invalidId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", Matchers.containsString("잘못된 요청 파라미터 타입입니다")));
    }

    @Test
    @DisplayName("유효한 요청 본문으로 요청 시 아이템 생성 할 수 있다.")
    public void createItem_Valid_Request() throws Exception {
        ItemCreateRequestDto requestDto = ItemCreateRequestDto.builder()
                .name("Sample Item")
                .price(100L)
                .stockQuantity(10)
                .build();

        ItemCreateResponseDto responseDto = ItemCreateResponseDto.builder()
                .id(1L)
                .name("Sample Item")
                .price(100L)
                .stockQuantity(10)
                .build();

        when(itemService.createItem(any(ItemCreateRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/v1/items")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.name").value("Sample Item"))
                .andExpect(jsonPath("$.data.price").value(100L))
                .andExpect(jsonPath("$.data.stockQuantity").value(10));
    }

    @Test
    @DisplayName("동일한 상품명 존재 시 아이템 생성에 실패한다.")
    public void createItem_Invalid_Request() throws Exception {

        ItemCreateRequestDto requestDto = ItemCreateRequestDto.builder()
                .name("Sample Item")
                .price(100L)
                .stockQuantity(10)
                .build();

        when(itemService.createItem(any(ItemCreateRequestDto.class)))
                .thenThrow(new CustomException(BAD_REQUEST, ITEM_NAME_DUPLICATION));

        mockMvc.perform(post("/v1/items")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("유효한 아이템 ID와 요청 본문으로 아이템 정보 업데이트")
    public void updateItem_Valid_Id_And_Request() throws Exception {
        Long id = 1L;
        ItemUpdateRequestDto requestDto = ItemUpdateRequestDto.builder()
                .price(200L)
                .build();

        // Mock the service call to return a valid ItemUpdateResponseDto
        ItemUpdateResponseDto responseDto = ItemUpdateResponseDto.builder()
                .id(id)
                .name("Sample Item")
                .beforePrice(100L)
                .afterPrice(200L)
                .stockQuantity(10)
                .remainingStockQuantity(5)
                .build();

        when(itemService.updateItem(any(Long.class), any(ItemUpdateRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(put("/v1/items/" + id)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(id))
                .andExpect(jsonPath("$.data.beforePrice").value(100L))
                .andExpect(jsonPath("$.data.afterPrice").value(200L));
    }

    @Test
    @DisplayName("존재하지 않는 아이템 ID로 아이템 정보 업데이트 시 예외가 발생한다.")
    public void updateItem_Invalid_Id() throws Exception {
        Long invalidId = 999L;
        ItemUpdateRequestDto requestDto = ItemUpdateRequestDto.builder()
                .price(200L)
                .build();

        when(itemService.updateItem(eq(invalidId), any(ItemUpdateRequestDto.class))).thenThrow(new Exception(new CustomException(NOT_FOUND, ITEM_NOT_FOUND)));

        mockMvc.perform(put("/v1/items/" + invalidId)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("동일한 가격으로 아이템 정보 업데이트 시 예외가 발생한다.")
    public void updateItem_Same_Price() throws Exception {
        Long id = 1L;
        ItemUpdateRequestDto requestDto = ItemUpdateRequestDto.builder()
                .price(100L) // Assuming the current price is also 100L
                .build();

        when(itemService.updateItem(eq(id), any(ItemUpdateRequestDto.class))).thenThrow(new CustomException(BAD_REQUEST, ITEM_PRICE_NOT_CHANGED));

        mockMvc.perform(put("/v1/items/" + id)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("유효한 아이템 ID로 아이템 삭제")
    public void deleteItem_Valid_Id() throws Exception {
        Long id = 1L;
        ItemDeleteResponseDto responseDto = ItemDeleteResponseDto.builder()
                .id(id)
                .name("Sample Item")
                .remainingStockQuantity(5)
                .deletedAt(now())
                .build();

        when(itemService.deleteItem(id)).thenReturn(responseDto);

        mockMvc.perform(delete("/v1/items/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(id))
                .andExpect(jsonPath("$.data.name").value("Sample Item"))
                .andExpect(jsonPath("$.data.remainingStockQuantity").value(5));
    }

    @Test
    @DisplayName("존재하지 않는 아이템 ID로 아이템 삭제 시 예외가 발생한다.")
    public void deleteItem_Invalid_Id() throws Exception {
        Long invalidId = 999L;

        when(itemService.deleteItem(invalidId)).thenThrow(new Exception(new CustomException(NOT_FOUND, ITEM_NOT_FOUND)));

        mockMvc.perform(delete("/v1/items/" + invalidId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("주문된 아이템을 삭제하려고 시도할 때 예외가 발생한다.")
    public void deleteItem_Ordered_Item() throws Exception {
        Long id = 2L;

        when(itemService.deleteItem(id)).thenThrow(new CustomException(BAD_REQUEST, ITEM_DELETE_NOT_POSSIBLE));

        mockMvc.perform(delete("/v1/items/" + id))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("유효한 아이템 ID와 날짜로 특정 시점의 아이템 가격 조회")
    public void selectItemPriceAtTime_Valid_Id_And_Date() throws Exception {
        Long id = 1L;
        String date = "2023-08-20";
        ItemPriceAtTimeResponseDto responseDto = ItemPriceAtTimeResponseDto.builder()
                .id(id)
                .name("Sample Item")
                .date(date)
                .price(150L)
                .currentPrice(200L)
                .build();

        when(itemService.selectItemPriceAtTime(id, date)).thenReturn(responseDto);

        mockMvc.perform(get("/v1/items/" + id + "/price")
                        .param("date", date))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(id))
                .andExpect(jsonPath("$.data.name").value("Sample Item"))
                .andExpect(jsonPath("$.data.date").value(date))
                .andExpect(jsonPath("$.data.price").value(150L))
                .andExpect(jsonPath("$.data.currentPrice").value(200L));
    }

    @Test
    @DisplayName("존재하지 않는 아이템 ID로 특정 시점의 아이템 가격 조회 시 예외가 발생한다.")
    public void selectItemPriceAtTime_Invalid_Id() throws Exception {
        Long invalidId = 999L;
        String date = "2023-08-20";

        when(itemService.selectItemPriceAtTime(invalidId, date)).thenThrow(new Exception(new CustomException(NOT_FOUND, ITEM_NOT_FOUND)));

        mockMvc.perform(get("/v1/items/" + invalidId + "/price")
                        .param("date", date))
                .andExpect(status().isNotFound());
    }
}
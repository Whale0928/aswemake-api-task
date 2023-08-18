package com.aswemake.api.aswemakeapitask.restDocs;

import com.aswemake.api.aswemakeapitask.controller.ItemController;
import com.aswemake.api.aswemakeapitask.dto.GlobalResponse;
import com.aswemake.api.aswemakeapitask.dto.item.request.ItemCreateRequestDto;
import com.aswemake.api.aswemakeapitask.dto.item.request.ItemUpdateRequestDto;
import com.aswemake.api.aswemakeapitask.dto.item.response.ItemCreateResponseDto;
import com.aswemake.api.aswemakeapitask.dto.item.response.ItemDeleteResponseDto;
import com.aswemake.api.aswemakeapitask.dto.item.response.ItemPriceAtTimeResponseDto;
import com.aswemake.api.aswemakeapitask.dto.item.response.ItemSelectResponseDto;
import com.aswemake.api.aswemakeapitask.dto.item.response.ItemUpdateResponseDto;
import com.aswemake.api.aswemakeapitask.service.ItemService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.restdocs.payload.JsonFieldType;

import java.time.LocalDateTime;

import static java.time.LocalDateTime.now;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ItemControllerRestDocsTest extends RestDocsSupport {
    @InjectMocks
    private ItemController itemController;

    @Mock
    private ItemService itemService;

    @Override
    protected Object initController() {
        return new ItemController(itemService);
    }

    @Test
    @DisplayName("상품 정보 조회")
    void selectItem() throws Exception {
        Long id = 1L;
        ItemSelectResponseDto responseDto = ItemSelectResponseDto.builder()
                .id(id)
                .name("테스트 상품_AAA")
                .price(1000L)
                .stockQuantity(100)
                .remainingStockQuantity(50)
                .build();

        GlobalResponse response = GlobalResponse
                .builder()
                .status(OK)
                .timestamp(now())
                .message("상품 정보 성공")
                .data(responseDto)
                .build();

        when(itemService.selectItem(id)).thenReturn(responseDto);

        mockMvc.perform(get("/v1/items/{id}", id)
                        .pathInfo("/v1/items/1")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("items/select",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("상품 아이디")
                        ),
                        responseFields(
                                fieldWithPath("status").ignored(),
                                fieldWithPath("timestamp").ignored(),
                                fieldWithPath("message").ignored(),
                                fieldWithPath("data").ignored(),
                                fieldWithPath("data.id").description("상품 아이디").type(JsonFieldType.NUMBER),
                                fieldWithPath("data.name").description("상품 이름").type(JsonFieldType.STRING),
                                fieldWithPath("data.price").description("상품 단가 가격").type(JsonFieldType.NUMBER),
                                fieldWithPath("data.stockQuantity").description("상품 재고 수량").type(JsonFieldType.NUMBER),
                                fieldWithPath("data.remainingStockQuantity").description("상품 남은 현재고 수량").type(JsonFieldType.NUMBER)
                        )
                ));
    }

    @Test
    @DisplayName("상품 생성")
    void createItem() throws Exception {
        Long id = 1L;
        String name = "생성된_상품_AAA";
        Long price = 12000L;
        int stockQuantity = 100;

        ItemCreateRequestDto requestDto = ItemCreateRequestDto.builder()
                .name(name)
                .price(price)
                .stockQuantity(stockQuantity)
                .build();

        ItemCreateResponseDto responseDto = ItemCreateResponseDto.builder()
                .id(id)
                .name(name)
                .price(price)
                .stockQuantity(stockQuantity)
                .build();

        GlobalResponse response = GlobalResponse
                .builder()
                .status(CREATED)
                .timestamp(now())
                .message("상품 생성 성공")
                .data(responseDto)
                .build();

        when(itemService.createItem(any(ItemCreateRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/v1/items")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(document("items/created",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("name").description("상품 이름"),
                                fieldWithPath("price").description("상품 단가 가격"),
                                fieldWithPath("stockQuantity").description("상품 재고 수량")
                        ),
                        responseFields(
                                fieldWithPath("status").ignored(),
                                fieldWithPath("timestamp").ignored(),
                                fieldWithPath("message").ignored(),
                                fieldWithPath("data").ignored(),
                                fieldWithPath("data.id").description("생성된 상품 아이디").type(JsonFieldType.NUMBER),
                                fieldWithPath("data.name").description("생성된 상품 이름").type(JsonFieldType.STRING),
                                fieldWithPath("data.price").description("생성된 상품 단가 가격").type(JsonFieldType.NUMBER),
                                fieldWithPath("data.stockQuantity").description("생성된 상품 재고 수량").type(JsonFieldType.NUMBER)
                        )
                ));
    }

    @Test
    @DisplayName("상품 가격 수정")
    void updateItem() throws Exception {
        Long id = 1L;
        int changedPrice = 15000;
        ItemUpdateRequestDto requestDto = ItemUpdateRequestDto.builder()
                .price(changedPrice)
                .build();

        GlobalResponse response = GlobalResponse
                .builder()
                .status(OK)
                .timestamp(now())
                .message("상품 가격 수정 성공")
                .data(ItemUpdateResponseDto.builder()
                        .id(1L)
                        .name("테스트 상품_AAA")
                        .beforePrice(1000)
                        .afterPrice(1500)
                        .stockQuantity(100)
                        .remainingStockQuantity(60)
                        .build())
                .build();

        mockMvc.perform(put("/v1/items/{id}", id)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("items/update",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("수정 대상 상품 아이디")
                        ),
                        requestFields(
                                fieldWithPath("price").description("변경 할 가격")
                        ),
                        responseFields(
                                fieldWithPath("status").ignored(),
                                fieldWithPath("timestamp").ignored(),
                                fieldWithPath("message").ignored(),
                                fieldWithPath("data").ignored(),
                                fieldWithPath("data.id").description("수정 상품 아이디").type(JsonFieldType.NUMBER),
                                fieldWithPath("data.name").description("수정 상품 이름"),
                                fieldWithPath("data.beforePrice").description("수정 전 가격").type(JsonFieldType.NUMBER),
                                fieldWithPath("data.afterPrice").description("수정 후 가격").type(JsonFieldType.NUMBER),
                                fieldWithPath("data.stockQuantity").description("상품 재고 수량").type(JsonFieldType.NUMBER),
                                fieldWithPath("data.remainingStockQuantity").description("상품 재고 수량").type(JsonFieldType.NUMBER)
                        )
                ));
    }

    @Test
    @DisplayName("상품 삭제")
    void deleteItem() throws Exception {
        Long id = 1L;
        int changedPrice = 15000;
        GlobalResponse response = GlobalResponse
                .builder()
                .status(OK)
                .timestamp(now())
                .message("상품 삭제 성공")
                .data(ItemDeleteResponseDto.builder()
                        .id(1L)
                        .name("테스트 상품_AAA")
                        .remainingStockQuantity(60)
                        .deletedAt(LocalDateTime.now())
                        .build())
                .build();

        mockMvc.perform(delete("/v1/items/{id}", id))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("items/delete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("삭제 대상 상품 아이디")
                        ),
                        responseFields(
                                fieldWithPath("status").ignored(),
                                fieldWithPath("timestamp").ignored(),
                                fieldWithPath("message").ignored(),
                                fieldWithPath("data").ignored(),
                                fieldWithPath("data.id").description("삭제 상품 아이디").type(JsonFieldType.NUMBER),
                                fieldWithPath("data.name").description("삭제 상품 이름"),
                                fieldWithPath("data.remainingStockQuantity").description("삭제 시점 잔여 재고 수량"),
                                fieldWithPath("data.deletedAt").description("삭제 처리 시점")
                        )
                ));
    }

    @Test
    @DisplayName("특정 시점 아이템 조회")
    void selectItemPriceAtTime() throws Exception {
        Long id = 1L;
        String date = "2021-10-10";

        GlobalResponse response = GlobalResponse.builder()
                .status(OK)
                .message("조회 성공")
                .data(ItemPriceAtTimeResponseDto.builder()
                        .id(1L)
                        .name("테스트 상품_AAA")
                        .date("2021-10-10")
                        .price(8500)
                        .currentPrice(11000)
                        .build())
                .build();

        mockMvc.perform(get("/v1/items/{id}/price", id)
                        .param("date", date)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("items/select/price",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("date").description("조회 기준 시점")  // 쿼리 파라미터 설명 추가
                        ),
                        pathParameters(
                                parameterWithName("id").description("상품 아이디")
                        ),
                        responseFields(
                                fieldWithPath("status").ignored(),
                                fieldWithPath("timestamp").ignored(),
                                fieldWithPath("message").ignored(),
                                fieldWithPath("data").ignored(),
                                fieldWithPath("data.id").description("상품 아이디").type(JsonFieldType.NUMBER),
                                fieldWithPath("data.name").description("상품 이름").type(JsonFieldType.STRING),
                                fieldWithPath("data.date").description("조회 기준 시점").type(JsonFieldType.STRING),
                                fieldWithPath("data.price").description("조회 기준 시점 상품 가격").type(JsonFieldType.NUMBER),
                                fieldWithPath("data.currentPrice").description("현재 시점 가격").type(JsonFieldType.NUMBER)
                        )
                ));
    }
}
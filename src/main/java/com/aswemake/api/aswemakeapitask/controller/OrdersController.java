package com.aswemake.api.aswemakeapitask.controller;


import com.aswemake.api.aswemakeapitask.dto.GlobalResponse;
import com.aswemake.api.aswemakeapitask.dto.orders.request.OrderCreateRequestDto;
import com.aswemake.api.aswemakeapitask.dto.users.response.UserLoginInfo;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/orders")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class OrdersController {

    // TODO : POST /orders: 주문 생성
    @PostMapping("/{fundingId}")
    public ResponseEntity<GlobalResponse> createOrder(@PathVariable Long fundingId
            , @Valid @RequestBody OrderCreateRequestDto orderCreateRequestDto
            , HttpSession session) {


        // 세션에서 사용자 정보 가져오기
        UserLoginInfo userInfo = (UserLoginInfo) session.getAttribute("userInfo");

        // 세션에서 사용자 정보가 없는 경우 (로그인하지 않은 경우)
        if (userInfo.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(GlobalResponse.builder()
                            .status(HttpStatus.UNAUTHORIZED)
                            .message("로그인이 필요합니다.")
                            .build());
        }

        // 주문 생성에 성공한 경우 응답 반환
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(GlobalResponse.builder()
                        .status(HttpStatus.CREATED)
                        .message("주문 생성 성공")
                        .data(null)
                        .build());
    }

    // TODO : GET /orders/{id}: 주문 정보 조회
    @GetMapping("{id}")
    public ResponseEntity<GlobalResponse> selectOrder() {
        return null;
    }

    // TODO : GET /orders/{id}/total: 주문에 대한 총 금액 계산
    @GetMapping("{id}/total")
    public ResponseEntity<GlobalResponse> calculateTotalPrice() {
        return null;
    }

    @GetMapping("{id}/payment")
    public ResponseEntity<GlobalResponse> calculatePaymentPrice() {
        // TODO : GET /orders/{id}/payment: 주문에 대한 필요 결제 금액 계산
        return null;
    }
}

package com.aswemake.api.aswemakeapitask.domain.user;


import com.aswemake.api.aswemakeapitask.common.entity.BaseEntity;
import com.aswemake.api.aswemakeapitask.domain.orders.Orders;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Users extends BaseEntity {
    // 사용자 이름
    @Column(nullable = false)
    private String name;

    // 이메일 주소
    @Column(nullable = false, unique = true)
    private String email;

    // 비밀번호
    @Column(nullable = false)
    private String password;

    // 권한: 마트 or 일반 사용자
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    // 사용자의 경우 주문상품들
    @Builder.Default
    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL)
    private List<Orders> orderList = new ArrayList<>(); // 주문한 상품들의 목록
}
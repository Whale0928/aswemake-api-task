package com.aswemake.api.aswemakeapitask.domain.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserRole {
    MARKET("ROLE_MARKET"),  // 마트 권한
    USER("ROLE_USER");  // 일반 사용자 권한

    private final String authority;
}

package com.aswemake.api.aswemakeapitask.dto.users.response;


import com.aswemake.api.aswemakeapitask.domain.user.UserRole;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserLoginInfo {
    private UserRole role;
    private Long id;
    private String email;
    private String name;
    private String sessionId;

    @JsonIgnore
    public boolean isEmpty() {
        return this.id == null;
    }
}

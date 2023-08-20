package com.aswemake.api.aswemakeapitask.restDocs;


import com.aswemake.api.aswemakeapitask.controller.AuthController;
import com.aswemake.api.aswemakeapitask.domain.user.UserRole;
import com.aswemake.api.aswemakeapitask.dto.users.request.LoginRequestDto;
import com.aswemake.api.aswemakeapitask.dto.users.response.UserLoginInfo;
import com.aswemake.api.aswemakeapitask.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerRestDocsTest extends RestDocsSupport {

    @InjectMocks
    private AuthController authController;
    @Mock
    private AuthService authService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserDetailsService userDetailsService;

    @Override
    protected Object initController() {
        return new AuthController(authService, authenticationManager, userDetailsService);
    }

    @Test
    @DisplayName("일반 사용자 로그인")
    void loginByUser() throws Exception {
        LoginRequestDto requestDto = LoginRequestDto.builder()
                .email("userEmail@gmail.com")
                .password("qwe123")
                .build();

        UserLoginInfo userLoginInfo = UserLoginInfo.builder()
                .id(1L)
                .email("userEmail@gmail.com")
                .name("users")
                .role(UserRole.USER)
                .sessionId("test-Session-Id")
                .build();

        when(authService.loginByUser(any(LoginRequestDto.class))).thenReturn(userLoginInfo);

        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username("userEmail@gmail.com")
                .password("qwe123")
                .authorities(UserRole.USER.toString())
                .build();

        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
        when(authenticationManager.authenticate(any())).thenReturn(mock(Authentication.class));

        mockMvc.perform(post("/v1/auth/users/login")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("auth/login-user",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("email").description("사용자 로그인 이메일"),
                                fieldWithPath("password").description("사용자 비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("status").ignored(),
                                fieldWithPath("timestamp").ignored(),
                                fieldWithPath("message").ignored(),
                                fieldWithPath("data").ignored(),
                                fieldWithPath("data.role").description("역할"),
                                fieldWithPath("data.id").description("사용자 아이디").type(JsonFieldType.NUMBER),
                                fieldWithPath("data.email").description("사용자 이메일"),
                                fieldWithPath("data.name").description("사용자 이름"),
                                fieldWithPath("data.sessionId").description("세션 아이디").type(JsonFieldType.STRING)
                        )
                ));
    }


    @Test
    @DisplayName("마켓 사용자 로그인")
    void loginByMarket() throws Exception {
        LoginRequestDto requestDto = LoginRequestDto.builder()
                .email("market@market.com")
                .password("qwe123")
                .build();

        UserLoginInfo userLoginInfo = UserLoginInfo.builder()
                .id(1L)
                .email("market@market.com")
                .name("users")
                .role(UserRole.MARKET)
                .sessionId("test-Session-Id")
                .build();

        when(authService.loginByMarket(any(LoginRequestDto.class))).thenReturn(userLoginInfo);

        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username("market@market.com")
                .password("qwe123")
                .authorities(UserRole.MARKET.toString())
                .build();

        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
        when(authenticationManager.authenticate(any())).thenReturn(mock(Authentication.class));

        mockMvc.perform(post("/v1/auth/market/login")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("auth/login-market",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("email").description("마켓 로그인 이메일"),
                                fieldWithPath("password").description("마켓 비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("status").ignored(),
                                fieldWithPath("timestamp").ignored(),
                                fieldWithPath("message").ignored(),
                                fieldWithPath("data").ignored(),
                                fieldWithPath("data.role").description("역할"),
                                fieldWithPath("data.id").description("마켓 아이디").type(JsonFieldType.NUMBER),
                                fieldWithPath("data.email").description("마켓 이메일"),
                                fieldWithPath("data.name").description("마켓 이름"),
                                fieldWithPath("data.sessionId").description("세션 아이디").type(JsonFieldType.STRING)
                        )
                ));
    }
}
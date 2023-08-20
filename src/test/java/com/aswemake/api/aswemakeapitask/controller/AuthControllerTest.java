package com.aswemake.api.aswemakeapitask.controller;

import com.aswemake.api.aswemakeapitask.dto.users.request.LoginRequestDto;
import com.aswemake.api.aswemakeapitask.dto.users.response.UserLoginInfo;
import com.aswemake.api.aswemakeapitask.exception.CustomException;
import com.aswemake.api.aswemakeapitask.restDocs.RestDocsSupport;
import com.aswemake.api.aswemakeapitask.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import static com.aswemake.api.aswemakeapitask.domain.user.UserRole.MARKET;
import static com.aswemake.api.aswemakeapitask.domain.user.UserRole.USER;
import static com.aswemake.api.aswemakeapitask.exception.ErrorMessages.USER_NOT_FOUND;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest extends RestDocsSupport {

    private final String USER_EMAIL = "user@test.com";
    private final String MARKET_EMAIL = "market@market.com";
    private final String USER_PASSWORD = "qwe123";
    private final String MARKET_PASSWORD = "qwe123";
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
    @DisplayName("일반 사용자는 로그인할 수 있다.")
    void loginByUserTest() throws Exception {
        LoginRequestDto requestDto = LoginRequestDto.builder()
                .email(USER_EMAIL)
                .password(USER_PASSWORD)
                .build();

        UserLoginInfo userLoginInfo = UserLoginInfo.builder()
                .id(1L)
                .email(USER_EMAIL)
                .name("users")
                .role(USER)
                .sessionId("test-Session-Id")
                .build();

        when(authService.loginByUser(any(LoginRequestDto.class))).thenReturn(userLoginInfo);

        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(USER_EMAIL)
                .password("qwe123")
                .authorities(USER.toString())
                .build();

        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
        when(authenticationManager.authenticate(any())).thenReturn(mock(Authentication.class));

        mockMvc.perform(post("/v1/auth/users/login")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.email").value(USER_EMAIL))
                .andExpect(jsonPath("$.data.role").value(USER.toString()));
    }

    @Test
    @DisplayName("잘못된 입력값(이메일)이 들어오면 사용자 로그인에 실패한다")
    void loginUser_With_Invalid_Email_Test() throws Exception {
        // 잘못된 이메일 형식으로 로그인 요청 DTO 생성
        LoginRequestDto invalidEmailDto = LoginRequestDto.builder()
                .email("invalidEmailWithoutAtSign")
                .password(USER_PASSWORD)
                .build();

        // 로그인 시도
        mockMvc.perform(post("/v1/auth/users/login")
                        .content(objectMapper.writeValueAsString(invalidEmailDto))
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest()) // 잘못된 요청으로 인한 실패 응답 확인
                .andExpect(jsonPath("$.message").value("이메일 형식을 지켜주세요.")); // 실패 메시지 확인
    }

    @Test
    @DisplayName("잘못된 입력값(이메일)이 들어오면 사용자 로그인에 실패한다")
    void loginUser_With_Invalid_Password_Test() throws Exception {
        // 너무 짧은 비밀번호로 로그인 요청 DTO 생성
        LoginRequestDto shortPasswordDto = LoginRequestDto.builder()
                .email(USER_EMAIL)
                .password("1")
                .build();

        // 로그인 시도
        mockMvc.perform(post("/v1/auth/users/login")
                        .content(objectMapper.writeValueAsString(shortPasswordDto))
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest()) // 잘못된 요청으로 인한 실패 응답 확인
                .andExpect(jsonPath("$.message").value("비밀번호는 8자 이상 20자 이하로 입력해주세요.")); // 실패 메시지 확인
    }

    @Test
    @DisplayName("DB에 사용자 정보가 없으면 사용자 로그인에 실패한다")
    void login_user_with_non_existent_user_test() throws Exception {
        // DB에 없는 사용자 정보로 로그인 요청 DTO 생성
        LoginRequestDto nonExistentUserDto = LoginRequestDto.builder()
                .email("nonexistent@test.com")
                .password(USER_PASSWORD)
                .build();

        // authService의 loginByUser 메소드가 CustomException을 발생시키도록 설정
        when(authService.loginByUser(any(LoginRequestDto.class)))
                .thenThrow(new CustomException(NOT_FOUND, USER_NOT_FOUND));

        // 로그인 시도
        mockMvc.perform(post("/v1/auth/users/login")
                        .content(objectMapper.writeValueAsString(nonExistentUserDto))
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound()) // 사용자를 찾을 수 없음으로 인한 404 응답 확인
                .andExpect(jsonPath("$.message").value(USER_NOT_FOUND.getMessage())); // 실패 메시지 확인
    }

    @Test
    @DisplayName("올바른 마켓 정보로 로그인에 성공한다")
    void login_market_success_test() throws Exception {
        LoginRequestDto validMarketDto = LoginRequestDto.builder()
                .email(MARKET_EMAIL)
                .password(MARKET_PASSWORD)
                .build();

        UserLoginInfo marketLoginInfo = UserLoginInfo.builder()
                .id(1L)
                .email(MARKET_EMAIL)
                .name("market")
                .role(MARKET)
                .sessionId("test-Session-Id")
                .build();

        when(authService.loginByMarket(any(LoginRequestDto.class))).thenReturn(marketLoginInfo);

        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(MARKET_EMAIL)
                .password(MARKET_PASSWORD)
                .authorities(USER.toString())
                .build();

        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
        when(authenticationManager.authenticate(any())).thenReturn(mock(Authentication.class));

        mockMvc.perform(post("/v1/auth/market/login")
                        .content(objectMapper.writeValueAsString(validMarketDto))
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.email").value(MARKET_EMAIL))
                .andExpect(jsonPath("$.data.role").value(MARKET.toString()));
    }

    @Test
    @DisplayName("잘못된 입력값(이메일)이 들어오면 마켓 로그인에 실패한다")
    void login_market_with_invalid_email_test() throws Exception {
        // 잘못된 이메일 형식으로 로그인 요청 DTO 생성
        LoginRequestDto invalidEmailDto = LoginRequestDto.builder()
                .email("invalidEmailWithoutAtSign")
                .password(USER_PASSWORD)
                .build();

        // 로그인 시도
        mockMvc.perform(post("/v1/auth/market/login")
                        .content(objectMapper.writeValueAsString(invalidEmailDto))
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest()) // 잘못된 요청으로 인한 실패 응답 확인
                .andExpect(jsonPath("$.message").value("이메일 형식을 지켜주세요.")); // 실패 메시지 확인
    }

    @Test
    @DisplayName("잘못된 입력값(비밀번호)이 들어오면 마켓 로그인에 실패한다")
    void login_market_with_invalid_password_test() throws Exception {
        // 너무 짧은 비밀번호로 로그인 요청 DTO 생성
        LoginRequestDto shortPasswordDto = LoginRequestDto.builder()
                .email(USER_EMAIL)
                .password("1")
                .build();

        // 로그인 시도
        mockMvc.perform(post("/v1/auth/market/login")
                        .content(objectMapper.writeValueAsString(shortPasswordDto))
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest()) // 잘못된 요청으로 인한 실패 응답 확인
                .andExpect(jsonPath("$.message").value("비밀번호는 8자 이상 20자 이하로 입력해주세요.")); // 실패 메시지 확인
    }
}

package com.aswemake.api.aswemakeapitask.service;

import com.aswemake.api.aswemakeapitask.domain.user.UserRepository;
import com.aswemake.api.aswemakeapitask.domain.user.UserRole;
import com.aswemake.api.aswemakeapitask.domain.user.Users;
import com.aswemake.api.aswemakeapitask.dto.users.request.LoginRequestDto;
import com.aswemake.api.aswemakeapitask.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder encoder;

    private Users mockUser;
    private Users mockMarket;

    @BeforeEach
    void setUp() {
        mockUser = Users.builder()
                .email("test@example.com")
                .password("password123")
                .role(UserRole.USER)
                .build();
        setField(mockUser, "id", 1L);
        mockMarket = Users.builder()
                .email("market@example.com")
                .password("password123")
                .role(UserRole.MARKET)
                .build();
        setField(mockMarket, "id", 2L);

    }

    @Test
    @DisplayName("유효한 요청 본문으로 사용자 로그인 요청 시 성공적으로 로그인한다.")
    void loginUserWithValidRequest() throws Exception {
        // Given
        LoginRequestDto requestDto = LoginRequestDto.builder()
                .email("test@example.com")
                .password("password123")
                .build();

        when(userRepository.findByEmailAndRole(anyString(), eq(UserRole.USER))).thenReturn(Optional.of(mockUser));
        when(encoder.matches(anyString(), anyString())).thenReturn(true);

        // When
        authService.loginByUser(requestDto);

        // Then
        verify(userRepository).findByEmailAndRole(anyString(), eq(UserRole.USER));
    }

    @Test
    @DisplayName("잘못된 비밀번호로 사용자 로그인 요청 시 예외가 발생한다.")
    void loginUserWithInvalidPassword() throws Exception {
        // Given
        LoginRequestDto requestDto = LoginRequestDto.builder()
                .email("test@example.com")
                .password("wrongPassword")
                .build();

        when(userRepository.findByEmailAndRole(anyString(), eq(UserRole.USER))).thenReturn(Optional.of(mockUser));
        when(encoder.matches(anyString(), anyString())).thenReturn(false);

        // Then
        assertThrows(CustomException.class, () -> {
            // When
            authService.loginByUser(requestDto);
        });
    }

    @Test
    @DisplayName("존재하지 않는 이메일로 사용자 로그인 요청 시 예외가 발생한다.")
    void loginUserWithNonExistentEmail() throws Exception {
        // Given
        LoginRequestDto requestDto = LoginRequestDto.builder()
                .email("nonexistent@example.com")
                .password("password123")
                .build();

        when(userRepository.findByEmailAndRole(anyString(), eq(UserRole.USER))).thenReturn(Optional.empty());

        // Then
        assertThrows(CustomException.class, () -> {
            // When
            authService.loginByUser(requestDto);
        });
    }

    @Test
    @DisplayName("유효한 요청 본문으로 마켓 로그인 요청 시 성공적으로 로그인한다.")
    void loginMarketWithValidRequest() throws Exception {
        // Given
        LoginRequestDto requestDto = LoginRequestDto.builder()
                .email("market@example.com")
                .password("password123")
                .build();

        when(userRepository.findByEmailAndRole(anyString(), eq(UserRole.MARKET))).thenReturn(Optional.of(mockMarket));
        when(encoder.matches(anyString(), anyString())).thenReturn(true);

        // When
        authService.loginByMarket(requestDto);

        // Then
        verify(userRepository).findByEmailAndRole(anyString(), eq(UserRole.MARKET));
    }

    @Test
    @DisplayName("잘못된 비밀번호로 마켓 로그인 요청 시 예외가 발생한다.")
    void loginMarketWithInvalidPassword() throws Exception {
        // Given
        LoginRequestDto requestDto = LoginRequestDto.builder()
                .email("market@example.com")
                .password("wrongPassword")
                .build();

        when(userRepository.findByEmailAndRole(anyString(), eq(UserRole.MARKET))).thenReturn(Optional.of(mockMarket));
        when(encoder.matches(anyString(), anyString())).thenReturn(false);

        // Then
        assertThrows(CustomException.class, () -> {
            // When
            authService.loginByMarket(requestDto);
        });
    }

    @Test
    @DisplayName("존재하지 않는 이메일로 마켓 로그인 요청 시 예외가 발생한다.")
    void loginMarketWithNonExistentEmail() throws Exception {
        // Given
        LoginRequestDto requestDto = LoginRequestDto.builder()
                .email("nonexistentmarket@example.com")
                .password("password123")
                .build();

        when(userRepository.findByEmailAndRole(anyString(), eq(UserRole.MARKET))).thenReturn(Optional.empty());

        // Then
        assertThrows(CustomException.class, () -> {
            // When
            authService.loginByMarket(requestDto);
        });
    }
}
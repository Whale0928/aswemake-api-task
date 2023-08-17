package com.aswemake.api.aswemakeapitask.controller;

import com.aswemake.api.aswemakeapitask.domain.user.UserRepository;
import com.aswemake.api.aswemakeapitask.domain.user.UserRole;
import com.aswemake.api.aswemakeapitask.domain.user.Users;
import com.aswemake.api.aswemakeapitask.dto.GlobalResponse;
import com.aswemake.api.aswemakeapitask.dto.users.request.LoginRequestDto;
import com.aswemake.api.aswemakeapitask.dto.users.response.UserLoginInfo;
import com.aswemake.api.aswemakeapitask.service.AuthService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static java.time.LocalDateTime.now;
import static org.springframework.http.ResponseEntity.ok;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/auth")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AuthController {

    private final AuthService authService;

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository; // 사용자 정보

    // TODO : 일반 사용자 로그인
    @PostMapping("/users/login")
    public ResponseEntity<GlobalResponse> loginUser(@RequestBody LoginRequestDto loginRequestDto, HttpSession session) {

        UserLoginInfo userInfo = UserLoginInfo.builder()
                .id(1L)
                .email("userEmail@Gmail.com")
                .name("김 사용자")
                .role(UserRole.USER)
                .sessionId("sessionId-test-1234")
                .build();

        session.setAttribute("userInfo", userInfo);

        return ok().body(GlobalResponse.builder()
                .status(HttpStatus.OK)
                .timestamp(now())
                .message("로그인 성공")
                .data(userInfo)
                .build());
    }

    // TODO : 마켓 사용자 로그인
    @PostMapping("/market/login")
    public ResponseEntity<GlobalResponse> loginMarket(@RequestBody @Valid LoginRequestDto loginRequestDto, HttpSession session) {
        //서비스 동작 후 존재하는게 확실한 유저 반환
        // 서비스 레이어 시작
        String email = loginRequestDto.getEmail();
        Users users = userRepository.findByEmail(email).orElse(null);

        // 서비스 레이어 끝
        UserDetails userDetails = userDetailsService.loadUserByUsername(users.getEmail());

        // 사용자 인증
        Authentication authentication = new UsernamePasswordAuthenticationToken(users.getEmail(), users.getPassword(), userDetails.getAuthorities());

        // 인증 정보 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserLoginInfo userInfo = UserLoginInfo.builder()
                .id(1L)
                .email(users.getEmail())
                .name(users.getName())
                .role(users.getRole())
                .sessionId("sessionId-test-1234")
                .build();

        session.setAttribute("userInfo", userInfo);

        return ok().body(GlobalResponse.builder()
                .status(HttpStatus.OK)
                .timestamp(now())
                .message("로그인 성공")
                .data(userInfo)
                .build());
    }
}

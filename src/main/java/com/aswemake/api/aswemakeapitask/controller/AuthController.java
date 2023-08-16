package com.aswemake.api.aswemakeapitask.controller;

import com.aswemake.api.aswemakeapitask.domain.user.UserRepository;
import com.aswemake.api.aswemakeapitask.domain.user.Users;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository; // 사용자 정보

    // todo-일반 사용자 로그인
    @PostMapping("/user/login")
    public ResponseEntity<?> loginUser(/*@RequestBody LoginRequest loginRequest*/) {
        return ResponseEntity.ok().body("User logged in successfully");
    }

    // todo-마켓 사용자 로그인
    @PostMapping("/market/login")
    public ResponseEntity<?> loginMarket(String email, HttpSession session) {
        //서비스 동작 후 존재하는게 확실한 유저 반환
        // 서비스 레이어 시작
        Users users = userRepository.findByEmail(email).get();
        // 서비스 레이어 끝

        UserDetails userDetails = userDetailsService.loadUserByUsername(users.getEmail());

        // 사용자 인증
        Authentication authentication = new UsernamePasswordAuthenticationToken(users.getEmail(), users.getPassword(), userDetails.getAuthorities());

        // 인증 정보 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);

        HashMap<String, Object> userInfo = new HashMap<>();

        userInfo.put("session-id", session.getId());
        userInfo.put("id", users.getId());
        userInfo.put("email", users.getEmail());
        userInfo.put("name", users.getName());
        userInfo.put("role", users.getRole());

        session.setAttribute("userInfo", userInfo);

        return ResponseEntity.ok().body(userInfo);
    }
}

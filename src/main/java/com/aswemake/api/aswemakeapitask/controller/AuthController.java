package com.aswemake.api.aswemakeapitask.controller;

import com.aswemake.api.aswemakeapitask.dto.GlobalResponse;
import com.aswemake.api.aswemakeapitask.dto.users.request.LoginRequestDto;
import com.aswemake.api.aswemakeapitask.dto.users.response.UserLoginInfo;
import com.aswemake.api.aswemakeapitask.service.AuthService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/auth")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AuthController {

    private final AuthService service;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    @PostMapping("/users/login")
    public ResponseEntity<GlobalResponse> loginUser(@RequestBody LoginRequestDto loginRequestDto, HttpSession session) throws Exception {
        UserLoginInfo userInfo = service.loginByUser(loginRequestDto);
        authenticateUser(loginRequestDto, session, userInfo);
        return GlobalResponse.ok("사용자 로그인 성공", userInfo);
    }

    @PostMapping("/market/login")
    public ResponseEntity<GlobalResponse> loginMarket(@RequestBody @Valid LoginRequestDto loginRequestDto, HttpSession session) throws Exception {
        UserLoginInfo userInfo = service.loginByMarket(loginRequestDto);
        authenticateUser(loginRequestDto, session, userInfo);
        return GlobalResponse.ok("마켓 로그인 성공", userInfo);
    }

    private void authenticateUser(LoginRequestDto loginRequestDto, HttpSession session, UserLoginInfo userInfo) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequestDto.getEmail());
        Authentication token = new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(), loginRequestDto.getPassword(), userDetails.getAuthorities());
        Authentication authentication = authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        userInfo.addSessionInfo(session.getId());
        session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
        session.setAttribute("userInfo", userInfo);
    }
}

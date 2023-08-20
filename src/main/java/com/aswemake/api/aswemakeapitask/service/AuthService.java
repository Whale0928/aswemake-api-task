package com.aswemake.api.aswemakeapitask.service;

import com.aswemake.api.aswemakeapitask.domain.user.UserRepository;
import com.aswemake.api.aswemakeapitask.domain.user.UserRole;
import com.aswemake.api.aswemakeapitask.dto.users.request.LoginRequestDto;
import com.aswemake.api.aswemakeapitask.dto.users.response.UserLoginInfo;
import com.aswemake.api.aswemakeapitask.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.aswemake.api.aswemakeapitask.exception.ErrorMessages.PASSWORD_NOT_MATCH;
import static com.aswemake.api.aswemakeapitask.exception.ErrorMessages.USER_NOT_FOUND;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    public UserLoginInfo loginByUser(LoginRequestDto loginRequestDto) throws Exception {
        return userRepository.findByEmailAndRole(loginRequestDto.getEmail(), UserRole.USER)
                .map(user -> {
                    if (encoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
                        log.info("이메일 {}로 성공적으로 로그인하였습니다.", loginRequestDto.getEmail());
                        return UserLoginInfo.builder()
                                .id(user.getId())
                                .email(user.getEmail())
                                .name(user.getName())
                                .role(user.getRole())
                                .build();
                    } else {
                        log.warn("이메일 {}로 로그인 시도가 실패하였습니다. 이유: 비밀번호 불일치.", loginRequestDto.getEmail());
                        throw new CustomException(BAD_REQUEST, PASSWORD_NOT_MATCH);
                    }
                })
                .orElseThrow(() -> {
                    log.warn("이메일 {}로 로그인 시도가 실패하였습니다. 이유: 사용자를 찾을 수 없음.", loginRequestDto.getEmail());
                    return new CustomException(NOT_FOUND, USER_NOT_FOUND);
                });
    }

    public UserLoginInfo loginByMarket(LoginRequestDto loginRequestDto) throws Exception {
        return userRepository.findByEmailAndRole(loginRequestDto.getEmail(), UserRole.MARKET)
                .map(user -> {
                    if (encoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
                        log.info("이메일 {}로 성공적으로 로그인하였습니다.", loginRequestDto.getEmail());
                        return UserLoginInfo.builder()
                                .id(user.getId())
                                .email(user.getEmail())
                                .name(user.getName())
                                .role(user.getRole())
                                .build();
                    } else {
                        log.warn("이메일 {}로 로그인 시도가 실패하였습니다. 이유: 비밀번호 불일치.", loginRequestDto.getEmail());
                        throw new CustomException(BAD_REQUEST, PASSWORD_NOT_MATCH);
                    }
                })
                .orElseThrow(() -> {
                    log.warn("이메일 {}로 로그인 시도가 실패하였습니다. 이유: 사용자를 찾을 수 없음.", loginRequestDto.getEmail());
                    return new CustomException(NOT_FOUND, USER_NOT_FOUND);
                });
    }
}


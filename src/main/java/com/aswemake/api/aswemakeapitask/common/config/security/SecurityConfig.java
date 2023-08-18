package com.aswemake.api.aswemakeapitask.common.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.Collections;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    @Bean
    public AuthenticationManager customAuthenticationManager() throws Exception {
        return new ProviderManager(Collections.singletonList(authenticationProvider()));
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());  // 비밀번호 인코더 설정
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                // 기존 -> .csrf().disable()
                // 수정 -> .csrf(AbstractHttpConfigurer::disable)
                // 권고 사항으로 인해 코드 수정
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .headers(AbstractHttpConfigurer::disable)//.frameOptions().disable()  // 프레임 사용 허용
                .formLogin(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/v1/auth/**")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/v1/coupons/**", "GET")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/v1/items/{id}/**", "GET")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/v1/items/**")).hasRole("MARKET")
                                .anyRequest().authenticated()
                )
                .build();
    }
}

//.requestMatchers(antMatcher(GET, "/market/**")).hasRole("MARKET")   //매장접근
//.requestMatchers(new AntPathRequestMatcher("/v1/auth/**")).permitAll()//로그인 허용
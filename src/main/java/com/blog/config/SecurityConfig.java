package com.blog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // 테스트 및 개발 단계에서 편리함을 위해 비활성화
            .authorizeHttpRequests(auth -> auth
                // 1. 누구나 접근 가능한 경로 (정적 리소스 포함)
                .requestMatchers("/", "/user/**", "/place/list", "/place/detail",
                               "/planner/form", "/planner/generate",
                               "/css/**", "/js/**", "/images/**", "/upload/**", "/favicon.ico").permitAll()
                // 2. 관리자 전용 경로
                .requestMatchers("/admin/**").hasRole("ADMIN")
                // 3. 그 외 모든 요청은 인증 필요
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/user/login")           // 사용자 정의 로그인 페이지
                .loginProcessingUrl("/loginProc")   // HTML Form의 action과 일치해야 함
                .defaultSuccessUrl("/", true)       // ★ 중요: 성공 시 무조건 메인으로 강제 리다이렉트
                .failureUrl("/user/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/user/logout")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );

        return http.build();
    }
}
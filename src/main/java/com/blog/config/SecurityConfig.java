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
                .csrf(csrf -> csrf.disable()) // テスト/開発便宜上無効化
                .authorizeHttpRequests(auth -> auth
                        // 1. 全体アクセス可能経路 (静的リソース等)
                        .requestMatchers("/", "/user/**", "/place/list", "/place/detail",
                                "/planner/form", "/planner/generate",
                                "/board/list", "/board/detail",
                                "/trip/hotels", "/hotel-search-page", "/api/hotels/**",
                                "/trip/flights", "/search-page", "/api/flights/**",
                                "/planner/save", "/planner/update",
                                "/css/**", "/js/**", "/images/**", "/upload/**", "/favicon.ico")
                        .permitAll()
                        // 2. 管理者専用経路
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        // 3. その他要認証
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/user/login") // カスタムログインページ
                        .loginProcessingUrl("/loginProc") // HTML Formのactionと一致
                        .defaultSuccessUrl("/", true) // ★ 重要: 成功時メインへ強制リダイレクト
                        .failureUrl("/user/login?error=true")
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/user/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll());

        return http.build();
    }
}
package com.blog.dto;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * 회원 DTO (users 테이블)
 * 로그인 정보 및 프로필 관리
 */
@Data
public class UserDto {
	private Long userId;
	private String username;
	private String password;
	private String nickname;
	private String role;
	private LocalDateTime createdAt;


}

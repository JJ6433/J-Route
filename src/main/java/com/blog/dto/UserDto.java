package com.blog.dto;

import java.time.LocalDateTime;

/**
 * 회원 DTO (users 테이블)
 * 로그인 정보 및 프로필 관리
 */
public class UserDto {
	private Long userId;
	private String username;
	private String password;
	private String nickname;
	private String role;
	private LocalDateTime createdAt;

	public Long getUserId() { return userId; }
	public void setUserId(Long userId) { this.userId = userId; }
	public String getUsername() { return username; }
	public void setUsername(String username) { this.username = username; }
	public String getPassword() { return password; }
	public void setPassword(String password) { this.password = password; }
	public String getNickname() { return nickname; }
	public void setNickname(String nickname) { this.nickname = nickname; }
	public String getRole() { return role; }
	public void setRole(String role) { this.role = role; }
	public LocalDateTime getCreatedAt() { return createdAt; }
	public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

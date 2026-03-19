package com.blog.dto;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * 会員 DTO (users テーブル)
 * ログイン情報及びプロフィール管理
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

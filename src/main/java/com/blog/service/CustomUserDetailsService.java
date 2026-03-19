package com.blog.service;

import com.blog.dto.UserDto;
import com.blog.mapper.UserMapper;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Spring Security ユーザー認証サービス
 * DBから username で会員照会後 UserDetails 返却
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

	private final UserMapper userMapper;

	public CustomUserDetailsService(UserMapper userMapper) {
		this.userMapper = userMapper;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserDto userDto = userMapper.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("ユーザーが見つかりません: " + username));

		String role = "ROLE_" + userDto.getRole();
		return User.builder()
				.username(userDto.getUsername())
				.password(userDto.getPassword())
				.authorities(Collections.singletonList(new SimpleGrantedAuthority(role)))
				.build();
	}
}

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
 * Spring Security 사용자 인증 서비스
 * DB에서 username으로 회원 조회 후 UserDetails 반환
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
				.orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));

		String role = "ROLE_" + userDto.getRole();
		return User.builder()
				.username(userDto.getUsername())
				.password(userDto.getPassword())
				.authorities(Collections.singletonList(new SimpleGrantedAuthority(role)))
				.build();
	}
}

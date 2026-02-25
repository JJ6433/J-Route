package com.blog.service;

import com.blog.dto.UserDto;
import com.blog.mapper.UserMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 회원 서비스
 * 회원가입, 로그인 조회, 마이페이지 수정/비밀번호 변경/탈퇴
 */
@Service
public class UserService {

	private final UserMapper userMapper;
	private final PasswordEncoder passwordEncoder;

	public UserService(UserMapper userMapper, PasswordEncoder passwordEncoder) {
		this.userMapper = userMapper;
		this.passwordEncoder = passwordEncoder;
	}

	/** 회원가입: ID 중복 체크 후 비밀번호 암호화하여 저장 */
	@Transactional
	public void registerUser(UserDto userDto) {
		Optional<UserDto> existing = userMapper.findByUsername(userDto.getUsername());
		if (existing.isPresent()) {
			throw new IllegalStateException("このIDは既に使用されています。");
		}
		userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
		userDto.setRole("USER");
		userMapper.insertUser(userDto);
	}

	/** username으로 회원 조회 (로그인용) */
	public Optional<UserDto> findByUsername(String username) {
		return userMapper.findByUsername(username);
	}

	/** userId로 회원 조회 (마이페이지) */
	public Optional<UserDto> findById(Long userId) {
		return userMapper.findById(userId);
	}

	/** 닉네임 수정 */
	@Transactional
	public void updateUser(Long userId, String nickname) {
		UserDto dto = userMapper.findById(userId).orElseThrow(() -> new IllegalArgumentException("会員が見つかりません。"));
		dto.setNickname(nickname);
		userMapper.updateUser(dto);
	}

	/** 비밀번호 변경: 현재 비밀번호 확인 후 새 비밀번호 암호화 저장 */
	@Transactional
	public void changePassword(Long userId, String currentRawPassword, String newRawPassword) {
		UserDto dto = userMapper.findById(userId).orElseThrow(() -> new IllegalArgumentException("会員が見つかりません。"));
		if (!passwordEncoder.matches(currentRawPassword, dto.getPassword())) {
			throw new IllegalArgumentException("現在のパスワードが一致しません。");
		}
		userMapper.updatePassword(userId, passwordEncoder.encode(newRawPassword));
	}

	/** 회원 탈퇴 */
	@Transactional
	public void withdraw(Long userId) {
		userMapper.deleteById(userId);
	}

	/** 관리자: 전체 회원 목록 */
	@Transactional(readOnly = true)
	public java.util.List<UserDto> findAll() {
		return userMapper.findAll();
	}

	/** 회원 수 */
	@Transactional(readOnly = true)
	public int count() {
		return userMapper.count();
	}

	/** 관리자: 필터링된 회원 목록 */
	@Transactional(readOnly = true)
	public java.util.List<UserDto> findWithFilters(String keyword, String role, String startDate, String endDate) {
		return userMapper.findWithFilters(keyword, role, startDate, endDate);
	}

	/** 최근 7일간 신규 가입자 통계 */
	@Transactional(readOnly = true)
	public java.util.List<java.util.Map<String, Object>> getDailyRegistrationStats() {
		return userMapper.getDailyRegistrationStats();
	}
}

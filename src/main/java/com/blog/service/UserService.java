package com.blog.service;

import com.blog.dto.UserDto;
import com.blog.mapper.UserMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 会員サービス
 * 会員登録、ログイン照会、マイページ修正/PW変更/退会
 */
@Service
public class UserService {

	private final UserMapper userMapper;
	private final PasswordEncoder passwordEncoder;

	public UserService(UserMapper userMapper, PasswordEncoder passwordEncoder) {
		this.userMapper = userMapper;
		this.passwordEncoder = passwordEncoder;
	}

	/** 会員登録（ID重複確認・PW暗号化） */
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

	/** ログイン用会員照会 */
	public Optional<UserDto> findByUsername(String username) {
		return userMapper.findByUsername(username);
	}

	/** マイページ用会員照会 */
	public Optional<UserDto> findById(Long userId) {
		return userMapper.findById(userId);
	}

	/** ニックネーム修正 */
	@Transactional
	public void updateUser(Long userId, String nickname) {
		UserDto dto = userMapper.findById(userId).orElseThrow(() -> new IllegalArgumentException("会員が見つかりません。"));
		dto.setNickname(nickname);
		userMapper.updateUser(dto);
	}

	/** PW変更（現在PW確認・新PW暗号化） */
	@Transactional
	public void changePassword(Long userId, String currentRawPassword, String newRawPassword) {
		UserDto dto = userMapper.findById(userId).orElseThrow(() -> new IllegalArgumentException("会員が見つかりません。"));
		if (!passwordEncoder.matches(currentRawPassword, dto.getPassword())) {
			throw new IllegalArgumentException("現在のパスワードが一致しません。");
		}
		userMapper.updatePassword(userId, passwordEncoder.encode(newRawPassword));
	}

	/** 会員退会 */
	@Transactional
	public void withdraw(Long userId) {
		userMapper.deleteById(userId);
	}

	/** 管理者: 全会員リスト */
	@Transactional(readOnly = true)
	public java.util.List<UserDto> findAll() {
		return userMapper.findAll();
	}

	/** 会員数照会 */
	@Transactional(readOnly = true)
	public int count() {
		return userMapper.count();
	}

	/** 管理者: フィルタ適用会員リスト */
	@Transactional(readOnly = true)
	public java.util.List<UserDto> findWithFilters(String keyword, String role, String startDate, String endDate) {
		return userMapper.findWithFilters(keyword, role, startDate, endDate);
	}

	/** 新規加入者統計 */
	@Transactional(readOnly = true)
	public java.util.List<java.util.Map<String, Object>> getDailyRegistrationStats() {
		return userMapper.getDailyRegistrationStats();
	}
}

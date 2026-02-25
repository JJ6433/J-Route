package com.blog.mapper;

import com.blog.dto.UserDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

/**
 * 회원 MyBatis Mapper
 * 회원가입, 로그인 조회, 마이페이지 수정/탈퇴
 */
@Mapper
public interface UserMapper {

	void insertUser(UserDto userDto);

	Optional<UserDto> findByUsername(String username);

	/** 회원 한 명 조회 (마이페이지용) */
	Optional<UserDto> findById(@Param("userId") Long userId);

	/** 닉네임 등 정보 수정 (비밀번호 제외) */
	int updateUser(UserDto userDto);

	/** 비밀번호만 변경 */
	int updatePassword(@Param("userId") Long userId, @Param("encodedPassword") String encodedPassword);

	/** 회원 탈퇴 (삭제) */
	int deleteById(@Param("userId") Long userId);

	/** 관리자: 필터 및 일반 회원 목록 */
	List<UserDto> findAll();

	List<UserDto> findWithFilters(@Param("keyword") String keyword, @Param("role") String role,
			@Param("startDate") String startDate, @Param("endDate") String endDate);

	/** 회원 수 (대시보드) */
	int count();

	/** 최근 7일간 신규 가입자 통계 */
	List<java.util.Map<String, Object>> getDailyRegistrationStats();
}

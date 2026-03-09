package com.blog.mapper;

import com.blog.dto.UserDto;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 회원 MyBatis Mapper
 * 회원가입, 로그인 조회, 마이페이지 수정/탈퇴
 */
@Mapper
public interface UserMapper {

	@Insert("INSERT INTO users (username, password, nickname, role) " +
			"VALUES (#{username}, #{password}, #{nickname}, #{role})")
	@Options(useGeneratedKeys = true, keyProperty = "userId")
	void insertUser(UserDto userDto);

	@Select("SELECT user_id AS userId, username, password, nickname, role, created_at AS createdAt " +
			"FROM users WHERE username = #{username}")
	Optional<UserDto> findByUsername(String username);

	/** 회원 한 명 조회 (마이페이지용) */
	@Select("SELECT user_id AS userId, username, password, nickname, role, created_at AS createdAt " +
			"FROM users WHERE user_id = #{userId}")
	Optional<UserDto> findById(@Param("userId") Long userId);

	/** 닉네임 등 정보 수정 (비밀번호 제외) */
	@Update("UPDATE users SET nickname = #{nickname} WHERE user_id = #{userId}")
	int updateUser(UserDto userDto);

	/** 비밀번호만 변경 */
	@Update("UPDATE users SET password = #{encodedPassword} WHERE user_id = #{userId}")
	int updatePassword(@Param("userId") Long userId, @Param("encodedPassword") String encodedPassword);

	/** 회원 탈퇴 (삭제) */
	@Delete("DELETE FROM users WHERE user_id = #{userId}")
	int deleteById(@Param("userId") Long userId);

	/** 관리자: 필터 및 일반 회원 목록 */
	@Select("SELECT user_id AS userId, username, nickname, role, created_at AS createdAt " +
			"FROM users ORDER BY created_at DESC")
	List<UserDto> findAll();

	@Select("<script>" +
			"SELECT user_id AS userId, username, nickname, role, created_at AS createdAt FROM users " +
			"<where>" +
			"  <if test='keyword != null and keyword != \"\"'>" +
			"    AND (username LIKE CONCAT('%', #{keyword}, '%') OR nickname LIKE CONCAT('%', #{keyword}, '%'))" +
			"  </if>" +
			"  <if test='role != null and role != \"\"'>" +
			"    AND role = #{role}" +
			"  </if>" +
			"  <if test='startDate != null and startDate != \"\"'>" +
			"    AND created_at &gt;= #{startDate}" +
			"  </if>" +
			"  <if test='endDate != null and endDate != \"\"'>" +
			"    AND created_at &lt;= CONCAT(#{endDate}, ' 23:59:59')" +
			"  </if>" +
			"</where>" +
			"ORDER BY created_at DESC" +
			"</script>")
	List<UserDto> findWithFilters(@Param("keyword") String keyword, @Param("role") String role,
			@Param("startDate") String startDate, @Param("endDate") String endDate);

	/** 회원 수 (대시보드) */
	@Select("SELECT COUNT(*) FROM users")
	int count();

	/** 최근 7일간 신규 가입자 통계 */
	@Select("SELECT DATE_FORMAT(created_at, '%Y-%m-%d') as date, COUNT(*) as count " +
			"FROM users " +
			"WHERE created_at >= DATE_SUB(CURDATE(), INTERVAL 6 DAY) " +
			"GROUP BY DATE_FORMAT(created_at, '%Y-%m-%d') " +
			"ORDER BY date ASC")
	List<Map<String, Object>> getDailyRegistrationStats();
}

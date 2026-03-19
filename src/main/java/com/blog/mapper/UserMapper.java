package com.blog.mapper;

import com.blog.dto.UserDto;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 会員 MyBatis Mapper
 * 会員登録、ログイン照会、マイページ修正/退会
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

	/** 会員情報照会 */
	@Select("SELECT user_id AS userId, username, password, nickname, role, created_at AS createdAt " +
			"FROM users WHERE user_id = #{userId}")
	Optional<UserDto> findById(@Param("userId") Long userId);

	/** 情報修正（PW除く） */
	@Update("UPDATE users SET nickname = #{nickname} WHERE user_id = #{userId}")
	int updateUser(UserDto userDto);

	/** PW変更 */
	@Update("UPDATE users SET password = #{encodedPassword} WHERE user_id = #{userId}")
	int updatePassword(@Param("userId") Long userId, @Param("encodedPassword") String encodedPassword);

	/** 退会処理 */
	@Delete("DELETE FROM users WHERE user_id = #{userId}")
	int deleteById(@Param("userId") Long userId);

	/** 管理者: 会員リスト照会 */
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

	/** 会員数 */
	@Select("SELECT COUNT(*) FROM users")
	int count();

	/** 新規加入者統計 */
	@Select("SELECT DATE_FORMAT(created_at, '%Y-%m-%d') as date, COUNT(*) as count " +
			"FROM users " +
			"WHERE created_at >= DATE_SUB(CURDATE(), INTERVAL 6 DAY) " +
			"GROUP BY DATE_FORMAT(created_at, '%Y-%m-%d') " +
			"ORDER BY date ASC")
	List<Map<String, Object>> getDailyRegistrationStats();
}

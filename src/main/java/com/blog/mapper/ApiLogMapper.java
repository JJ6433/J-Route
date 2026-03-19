package com.blog.mapper;

import com.blog.dto.ApiLogDto;
import org.apache.ibatis.annotations.*;
import java.util.List;
import java.util.Map;

@Mapper
public interface ApiLogMapper {
    @Insert("INSERT INTO api_logs (api_name, endpoint, status, response_time, error_message) " +
            "VALUES (#{apiName}, #{endpoint}, #{status}, #{responseTime}, #{errorMessage})")
    @Options(useGeneratedKeys = true, keyProperty = "logId")
    void insertLog(ApiLogDto logDto);

    @Select("SELECT log_id AS logId, api_name AS apiName, endpoint, status, " +
            "response_time AS responseTime, error_message AS errorMessage, created_at AS createdAt " +
            "FROM api_logs ORDER BY created_at DESC")
    List<ApiLogDto> findAll();

    @Select("SELECT log_id AS logId, api_name AS apiName, endpoint, status, " +
            "response_time AS responseTime, error_message AS errorMessage, created_at AS createdAt " +
            "FROM api_logs ORDER BY created_at DESC LIMIT #{limit}")
    List<ApiLogDto> findLatest(int limit);

    @Delete("DELETE FROM api_logs")
    void deleteAll();

    /** API統計照会 */
    @Select("SELECT DATE_FORMAT(created_at, '%Y-%m-%d') as date, " +
            "COUNT(*) as total, " +
            "SUM(CASE WHEN status = 'SUCCESS' THEN 1 ELSE 0 END) as success, " +
            "SUM(CASE WHEN status = 'FAILURE' THEN 1 ELSE 0 END) as failure " +
            "FROM api_logs " +
            "WHERE created_at >= DATE_SUB(CURDATE(), INTERVAL 6 DAY) " +
            "GROUP BY DATE_FORMAT(created_at, '%Y-%m-%d') " +
            "ORDER BY date ASC")
    List<Map<String, Object>> getDailyStats();
}

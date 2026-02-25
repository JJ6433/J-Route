package com.blog.mapper;

import com.blog.dto.ApiLogDto;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface ApiLogMapper {
    void insertLog(ApiLogDto logDto);

    List<ApiLogDto> findAll();

    List<ApiLogDto> findLatest(int limit);

    void deleteAll();

    /** 최근 7일간 API 호출 통계 */
    List<java.util.Map<String, Object>> getDailyStats();
}

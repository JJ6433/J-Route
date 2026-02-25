package com.blog.service;

import com.blog.dto.ApiLogDto;
import com.blog.mapper.ApiLogMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ApiLogService {

    private final ApiLogMapper apiLogMapper;

    public ApiLogService(ApiLogMapper apiLogMapper) {
        this.apiLogMapper = apiLogMapper;
    }

    @Transactional
    public void saveLog(String apiName, String endpoint, String status, Long responseTime, String errorMessage) {
        ApiLogDto logDto = new ApiLogDto();
        logDto.setApiName(apiName);
        logDto.setEndpoint(endpoint);
        logDto.setStatus(status);
        logDto.setResponseTime(responseTime);
        logDto.setErrorMessage(errorMessage);
        apiLogMapper.insertLog(logDto);
    }

    @Transactional(readOnly = true)
    public List<ApiLogDto> getAllLogs() {
        return apiLogMapper.findAll();
    }

    @Transactional(readOnly = true)
    public List<ApiLogDto> getLatestLogs(int limit) {
        return apiLogMapper.findLatest(limit);
    }

    @Transactional
    public void clearLogs() {
        apiLogMapper.deleteAll();
    }

    @Transactional(readOnly = true)
    public List<java.util.Map<String, Object>> getDailyStats() {
        return apiLogMapper.getDailyStats();
    }
}

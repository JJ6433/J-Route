package com.blog.dto;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * API 呼び出しログ DTO
 */
@Data
public class ApiLogDto {
    private Long logId;
    private String apiName;
    private String endpoint;
    private String status;
    private Long responseTime;
    private String errorMessage;
    private LocalDateTime createdAt;

}
package com.blog.dto;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * お知らせ DTO
 */
@Data
public class NoticeDto {
    private Long noticeId;
    private String title;
    private String content;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Getters and Setters
    
}

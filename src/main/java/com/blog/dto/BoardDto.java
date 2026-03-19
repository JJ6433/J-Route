package com.blog.dto;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * 掲示板用 DTO
 * Lombok の代わりに明示的な Get/Set 使用で安定性確保
 */
@Data
public class BoardDto {
    private Integer boardId;
    private String title;
    private String content;
    private String authorNickname;
    private String thumbnailUrl;
    private Integer viewCount = 0;
    private Integer likeCount = 0;
    private LocalDateTime createdAt;
    private String region;
    private String duration;

    
    @Override
    public String toString() {
        return "BoardDto [boardId=" + boardId + ", title=" + title + ", authorNickname=" + authorNickname + "]";
    }
}
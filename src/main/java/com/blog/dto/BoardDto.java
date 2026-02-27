package com.blog.dto;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * 掲示板用 DTO
 * Lombok 대신 명시적인 Get/Set 사용으로 안정성 확보
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
package com.blog.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BoardDto {
    private Integer boardId;
    private String title;
    private String content;
    private String authorNickname;
    private String thumbnailUrl;
    private Integer viewCount;
    private Integer likeCount;
    private LocalDateTime createdAt;
    
    // 追加されたタグ用フィールド
    private String region;   // 地域 (東京、大阪など)
    private String duration; // 旅行日数 (2泊3日など)
}
package com.blog.dto;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * 掲示板コメント用 DTO
 * Lombok の代わりに明示的な Get/Set 使用で安定性確保
 */
@Data
public class CommentDto {
    private Integer commentId;
    private Integer boardId;
    private String content;
    private String authorNickname;
    private Integer parentId; // 親コメントID
    private LocalDateTime createdAt;

    // UIインデント処理用深度 (0: 一般, 1~5: 返信)
    private int depth;


    @Override
    public String toString() {
        return "CommentDto [commentId=" + commentId + ", boardId=" + boardId + ", authorNickname=" + authorNickname
                + "]";
    }
}
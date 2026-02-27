package com.blog.dto;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * 掲示판 댓글용 DTO
 * Lombok 대신 명시적인 Get/Set 사용으로 안정성 확보
 */
@Data
public class CommentDto {
    private Integer commentId;
    private Integer boardId;
    private String content;
    private String authorNickname;
    private Integer parentId; // 親コメントID
    private LocalDateTime createdAt;

    // UI의 인데트 처리를 위한 깊이 (0: 일반 댓글, 1~5: 답글)
    private int depth;


    @Override
    public String toString() {
        return "CommentDto [commentId=" + commentId + ", boardId=" + boardId + ", authorNickname=" + authorNickname
                + "]";
    }
}
package com.blog.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CommentDto {
    private Integer commentId;
    private Integer boardId;
    private String content;
    private String authorNickname;
    private Integer parentId; // 親コメントID
    private LocalDateTime createdAt;
    
    // UIのインデント処理のための深さ (0: 一般コメント, 1~5: 返信)
    private int depth; 
}
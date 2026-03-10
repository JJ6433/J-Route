package com.blog.mapper;

import com.blog.dto.CommentDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface CommentMapper {
    // コメントの登録
    void insertComment(CommentDto comment);
    // リスト取得
    List<CommentDto> getCommentsByBoardId(Integer boardId);
    // 単一コメント照会
    CommentDto getCommentById(Integer commentId);
    // コメントの削除
    void deleteComment(Integer commentId);
}
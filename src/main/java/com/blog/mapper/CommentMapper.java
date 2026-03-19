package com.blog.mapper;

import com.blog.dto.CommentDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface CommentMapper {
    // コメント登録
    void insertComment(CommentDto comment);
    // リスト取得
    List<CommentDto> getCommentsByBoardId(Integer boardId);
    // コメント詳細照会
    CommentDto getCommentById(Integer commentId);
    // コメント削除
    void deleteComment(Integer commentId);
}
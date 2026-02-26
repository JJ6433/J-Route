package com.blog.mapper;

import com.blog.dto.BoardDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface BoardMapper {
    void insertBoard(BoardDto board);
    
    // 検索条件（地域、日数）を追加したリスト取得
    List<BoardDto> getBoardList(@Param("limit") int limit, @Param("offset") int offset, 
                                @Param("region") String region, @Param("duration") String duration);
    
    // 検索条件を適用した全体件数の取得
    int getTotalCount(@Param("region") String region, @Param("duration") String duration);
    
    BoardDto getBoardById(Integer boardId);
    void updateViewCount(Integer boardId);
    void updateBoard(BoardDto board);
    void deleteBoard(Integer boardId);
    
    int checkUserLike(@Param("boardId") Integer boardId, @Param("username") String username);
    void insertUserLike(@Param("boardId") Integer boardId, @Param("username") String username);
    void deleteUserLike(@Param("boardId") Integer boardId, @Param("username") String username);
    void incrementLikeCount(Integer boardId);
    void decrementLikeCount(Integer boardId);
}
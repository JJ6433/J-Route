package com.blog.service;

import com.blog.dto.BoardDto;
import com.blog.mapper.BoardMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class BoardService {

    private final BoardMapper boardMapper;

    public BoardService(BoardMapper boardMapper) {
        this.boardMapper = boardMapper;
    }

    // 検索条件を含めたリスト取得
    public List<BoardDto> getList(int page, int size, String region, String duration) {
        int offset = (page - 1) * size;
        return boardMapper.getBoardList(size, offset, region, duration);
    }

    // 検索条件を含めた全体件数
    public int getTotal(String region, String duration) {
        return boardMapper.getTotalCount(region, duration);
    }

    // 詳細照会（照会数アップ）
    public BoardDto getDetail(Integer boardId) {
        boardMapper.updateViewCount(boardId);
        return boardMapper.getBoardById(boardId);
    }

    // 削除処理
    public void delete(Integer boardId) {
        boardMapper.deleteBoard(boardId);
    }

    // 登録・修正およびファイル保存
    public void save(BoardDto dto, MultipartFile file, String nickname, boolean isEdit) throws IOException {
        if (!isEdit) {
            dto.setAuthorNickname(nickname);
        }
        
        // サムネイル画像がある場合のみ処理
        if (file != null && !file.isEmpty()) {
            String path = System.getProperty("user.dir") + "/upload/";
            File dir = new File(path);
            if (!dir.exists()) dir.mkdirs();
            
            String name = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            file.transferTo(new File(path, name));
            dto.setThumbnailUrl("/upload/" + name);
        }
        
        if (isEdit) {
            boardMapper.updateBoard(dto);
        } else {
            boardMapper.insertBoard(dto);
        }
    }

    // いいね状態の確認
    public boolean isLiked(Integer boardId, String username) {
        if (username == null) return false;
        return boardMapper.checkUserLike(boardId, username) > 0;
    }

    // いいねのトグル処理（追加・削除）
    public void toggleLike(Integer boardId, String username) {
        if (boardMapper.checkUserLike(boardId, username) > 0) {
            boardMapper.deleteUserLike(boardId, username);
            boardMapper.decrementLikeCount(boardId);
        } else {
            boardMapper.insertUserLike(boardId, username);
            boardMapper.incrementLikeCount(boardId);
        }
    }
}
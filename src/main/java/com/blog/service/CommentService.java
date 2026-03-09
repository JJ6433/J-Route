package com.blog.service;

import com.blog.dto.CommentDto;
import com.blog.mapper.CommentMapper;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CommentService {

    private final CommentMapper commentMapper;

    public CommentService(CommentMapper commentMapper) {
        this.commentMapper = commentMapper;
    }

    public void addComment(CommentDto comment) {
        commentMapper.insertComment(comment);
    }

    // コメントリストを取得し、階層構造（深さ優先探索）に並び替える
    public List<CommentDto> getComments(Integer boardId) {
        List<CommentDto> allComments = commentMapper.getCommentsByBoardId(boardId);
        
        // 親IDをキーにしてグループ化
        Map<Integer, List<CommentDto>> commentMap = new HashMap<>();
        for (CommentDto c : allComments) {
            Integer parentId = (c.getParentId() == null) ? 0 : c.getParentId();
            commentMap.computeIfAbsent(parentId, k -> new ArrayList<>()).add(c);
        }

        List<CommentDto> sortedComments = new ArrayList<>();
        // 再帰的にリストを構築 (ルートコメントの親IDは0として処理)
        buildHierarchy(0, commentMap, sortedComments, 0);
        
        return sortedComments;
    }

    // 深さ優先探索 (DFS) でリストを平坦化
    private void buildHierarchy(Integer parentId, Map<Integer, List<CommentDto>> map, List<CommentDto> result, int depth) {
        List<CommentDto> children = map.getOrDefault(parentId, new ArrayList<>());
        for (CommentDto child : children) {
            child.setDepth(depth); // 現在の深さを保存
            result.add(child);
            // 子コメントがあればさらに深く探索
            buildHierarchy(child.getCommentId(), map, result, depth + 1);
        }
    }

    public CommentDto getCommentById(Integer commentId) {
        return commentMapper.getCommentById(commentId);
    }

    public void deleteComment(Integer commentId) {
        commentMapper.deleteComment(commentId);
    }
}
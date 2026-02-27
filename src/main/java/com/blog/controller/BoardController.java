package com.blog.controller;

import com.blog.dto.*;
import com.blog.service.BoardService;
import com.blog.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Controller
@RequestMapping("/board")
public class BoardController {
    
    private final BoardService boardService;
    private final CommentService commentService;

    public BoardController(BoardService boardService, CommentService commentService) { 
        this.boardService = boardService; 
        this.commentService = commentService;
    }

    // 掲示板リスト (タグ検索・ページング対応)
    @GetMapping("/list")
    public String list(@RequestParam(value = "page", defaultValue = "1") int page, 
                       @RequestParam(value = "region", required = false) String region,
                       @RequestParam(value = "duration", required = false) String duration,
                       Model model) {
        int size = 9;
        model.addAttribute("boardList", boardService.getList(page, size, region, duration));
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", (int) Math.ceil((double) boardService.getTotal(region, duration) / size));
        
        // 検索タグの維持
        model.addAttribute("selectedRegion", region);
        model.addAttribute("selectedDuration", duration);
        
        return "board/list";
    }

    // 詳細画面
    @GetMapping("/detail")
    public String detail(@RequestParam(value = "id") Integer boardId, Model model, Authentication auth) {
        model.addAttribute("board", boardService.getDetail(boardId));
        model.addAttribute("comments", commentService.getComments(boardId));
        model.addAttribute("isLiked", auth != null && boardService.isLiked(boardId, auth.getName()));
        return "board/detail";
    }

    // 登録画面
    @GetMapping("/write")
    public String writeForm() { 
        return "board/write"; 
    }

    // 登録処理
    @PostMapping("/write")
    public String writePro(BoardDto board, 
                           @RequestParam(value = "imageFile", required = false) MultipartFile imageFile, 
                           Authentication auth) throws IOException {
        boardService.save(board, imageFile, (auth != null) ? auth.getName() : "匿名", false);
        return "redirect:/board/list";
    }

    // 修正画面
    @GetMapping("/edit")
    public String editForm(@RequestParam(value = "id") Integer boardId, Model model, Authentication auth) {
        BoardDto board = boardService.getDetail(boardId);
        // 本人確認
        if (auth == null || !auth.getName().equals(board.getAuthorNickname())) {
            return "redirect:/board/list";
        }
        model.addAttribute("board", board);
        return "board/edit";
    }

    // 修正処理
    @PostMapping("/edit")
    public String editPro(BoardDto board, 
                          @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) throws IOException {
        boardService.save(board, imageFile, null, true);
        return "redirect:/board/detail?id=" + board.getBoardId();
    }

    // 削除処理
    @GetMapping("/delete")
    public String deletePro(@RequestParam(value = "id") Integer boardId, Authentication auth) {
        BoardDto board = boardService.getDetail(boardId);
        // 本人確認
        if (auth != null && auth.getName().equals(board.getAuthorNickname())) {
            boardService.delete(boardId);
        }
        return "redirect:/board/list";
    }

    // いいね処理
    @GetMapping("/like")
    public String likePro(@RequestParam(value = "id") Integer boardId, Authentication auth) {
        if (auth == null) return "redirect:/user/login"; // ログイン要求
        boardService.toggleLike(boardId, auth.getName());
        return "redirect:/board/detail?id=" + boardId;
    }

    // コメント登録
    @PostMapping("/comment/write")
    public String commentWrite(CommentDto comment, Authentication auth) {
        if (auth != null) {
            comment.setAuthorNickname(auth.getName());
            commentService.addComment(comment);
        }
        return "redirect:/board/detail?id=" + comment.getBoardId();
    }

    // コメント削除
    @GetMapping("/comment/delete")
    public String commentDelete(@RequestParam(value = "cId") Integer commentId, 
                                @RequestParam(value = "bId") Integer boardId, 
                                Authentication auth) {
        CommentDto comment = commentService.getCommentById(commentId);
        if (auth != null && comment != null && auth.getName().equals(comment.getAuthorNickname())) {
            commentService.deleteComment(commentId);
        }
        return "redirect:/board/detail?id=" + boardId;
    }

    // Summernoteエディター内の画像アップロード (Ajax)
    @PostMapping("/uploadImage")
    @ResponseBody
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String path = System.getProperty("user.dir") + "/upload/";
            File dir = new File(path);
            if (!dir.exists()) dir.mkdirs();

            String name = java.util.UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            file.transferTo(new File(path, name));
            
            return ResponseEntity.ok("/upload/" + name);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error");
        }
    }
}
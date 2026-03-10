package com.blog.controller.admin;

import com.blog.service.BoardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/board")
public class AdminBoardController {

    private final BoardService boardService;

    // 依存性の注入
    public AdminBoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    // 管理者用の掲示板リスト
    @GetMapping("/list")
    public String adminBoardList(@RequestParam(value = "page", defaultValue = "1") int page, Model model) {
        int size = 15;
        
        model.addAttribute("boardList", boardService.getList(page, size, null, null));
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", (int) Math.ceil((double) boardService.getTotal(null, null) / size));
        
        return "admin/boardList";
    }

    // 管理者による強制削除
    @GetMapping("/delete")
    public String adminBoardDelete(@RequestParam(value = "id") Integer boardId) {
        boardService.delete(boardId);
        return "redirect:/admin/board/list";
    }
}
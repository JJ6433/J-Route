package com.blog.controller;

import com.blog.dto.NoticeDto;
import com.blog.service.NoticeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/notice")
public class NoticeController {

    private final NoticeService noticeService;

    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable("id") Long id, Model model) {
        NoticeDto notice = noticeService.getNoticeById(id);

        // Only allow viewing active notices or if the user is an admin
        if (notice == null || (!notice.isActive())) {
            return "redirect:/";
        }

        model.addAttribute("notice", notice);
        return "notice/detail";
    }
}

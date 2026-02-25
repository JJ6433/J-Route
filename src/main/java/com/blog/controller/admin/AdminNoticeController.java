package com.blog.controller.admin;

import com.blog.dto.NoticeDto;
import com.blog.service.NoticeService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/notice")
@PreAuthorize("hasRole('ADMIN')")
public class AdminNoticeController {

    private final NoticeService noticeService;

    public AdminNoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("notices", noticeService.getAllNotices());
        return "admin/notices/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("notice", new NoticeDto());
        return "admin/notices/form";
    }

    @PostMapping("/new")
    public String create(@ModelAttribute NoticeDto notice, RedirectAttributes redirectAttributes) {
        try {
            noticeService.createNotice(notice);
            redirectAttributes.addFlashAttribute("message", "お知らせを登録しました。");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "登録に失敗しました: " + e.getMessage());
        }
        return "redirect:/admin/notice";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable("id") Long id, Model model) {
        NoticeDto notice = noticeService.getNoticeById(id);
        if (notice == null) {
            return "redirect:/admin/notice";
        }
        model.addAttribute("notice", notice);
        return "admin/notices/form";
    }

    @PostMapping("/edit/{id}")
    public String update(@PathVariable("id") Long id, @ModelAttribute NoticeDto notice,
            RedirectAttributes redirectAttributes) {
        try {
            notice.setNoticeId(id);
            noticeService.updateNotice(notice);
            redirectAttributes.addFlashAttribute("message", "お知らせを修正しました。");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "修正に失敗しました: " + e.getMessage());
        }
        return "redirect:/admin/notice";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            noticeService.deleteNotice(id);
            redirectAttributes.addFlashAttribute("message", "お知らせを削除しました。");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "削除に失敗しました: " + e.getMessage());
        }
        return "redirect:/admin/notice";
    }
}

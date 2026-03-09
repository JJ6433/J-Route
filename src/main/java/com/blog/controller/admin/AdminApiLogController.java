package com.blog.controller.admin;

import com.blog.service.ApiLogService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/api-log")
@PreAuthorize("hasRole('ADMIN')")
public class AdminApiLogController {

    private final ApiLogService apiLogService;

    public AdminApiLogController(ApiLogService apiLogService) {
        this.apiLogService = apiLogService;
    }

    @GetMapping
    public String logList(Model model) {
        model.addAttribute("logs", apiLogService.getAllLogs());
        return "admin/api_logs";
    }

    @PostMapping("/clear")
    public String clearLogs(RedirectAttributes redirectAttributes) {
        try {
            apiLogService.clearLogs();
            redirectAttributes.addFlashAttribute("message", "ログをすべて削除しました。");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "削除に失敗しました: " + e.getMessage());
        }
        return "redirect:/admin/api-log";
    }
}

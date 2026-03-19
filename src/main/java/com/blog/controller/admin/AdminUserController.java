package com.blog.controller.admin;

import com.blog.dto.UserDto;
import com.blog.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * 管理者会員管理
 * 全会員一覧, 強制退会
 */
@Controller
@RequestMapping("/admin/user")
public class AdminUserController {

	private final UserService userService;

	public AdminUserController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping("/list")
	public String list(
			@RequestParam(value = "keyword", required = false) String keyword,
			@RequestParam(value = "role", required = false) String role,
			@RequestParam(value = "startDate", required = false) String startDate,
			@RequestParam(value = "endDate", required = false) String endDate,
			Model model) {

		List<UserDto> users = userService.findWithFilters(keyword, role, startDate, endDate);
		model.addAttribute("users", users);
		model.addAttribute("keyword", keyword);
		model.addAttribute("role", role);
		model.addAttribute("startDate", startDate);
		model.addAttribute("endDate", endDate);

		return "admin/user/list";
	}

	@PostMapping("/delete")
	public String delete(@RequestParam("userId") Long userId, RedirectAttributes redirectAttributes) {
		userService.withdraw(userId);
		redirectAttributes.addFlashAttribute("message", "該当会員を退会させました。");
		return "redirect:/admin/user/list";
	}
}

package com.blog.controller.admin;

import com.blog.dto.PlaceDto;
import com.blog.service.PlaceService;
import com.blog.service.UserService;
import com.blog.service.ApiLogService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * 관리자 대시보드
 * 가입자 수, 여행지 수, 인기 여행지 통계
 */
@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

	private final UserService userService;
	private final PlaceService placeService;
	private final ApiLogService apiLogService;

	public AdminDashboardController(UserService userService, PlaceService placeService, ApiLogService apiLogService) {
		this.userService = userService;
		this.placeService = placeService;
		this.apiLogService = apiLogService;
	}

	@GetMapping({ "", "/" })
	public String dashboard(Model model) {
		int userCount = userService.count();
		int placeCount = placeService.count();
		List<PlaceDto> topPlaces = placeService.getTopPlaces(5);

		model.addAttribute("userCount", userCount);
		model.addAttribute("placeCount", placeCount);
		model.addAttribute("topPlaces", topPlaces);

		// Chart Data
		model.addAttribute("userStats", userService.getDailyRegistrationStats());
		model.addAttribute("apiStats", apiLogService.getDailyStats());

		return "admin/dashboard";
	}
}

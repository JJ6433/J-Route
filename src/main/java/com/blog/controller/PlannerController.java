package com.blog.controller;

import com.blog.dto.PlannerDto;
import com.blog.dto.UserDto;
import com.blog.service.PlannerService;
import com.blog.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * AI 旅行プランナーコントローラー
 * 入力フォーム、コース生成(モック)、結果タイムライン、保存
 */
@Controller
@RequestMapping("/planner")
public class PlannerController {

	private final PlannerService plannerService;
	private final UserService userService;

	@org.springframework.beans.factory.annotation.Value("${maps.api.key}")
	private String apiKey;

	public PlannerController(PlannerService plannerService, UserService userService) {
		this.plannerService = plannerService;
		this.userService = userService;
	}

	@GetMapping("/form")
	public String form() {
		return "planner/form";
	}

	@PostMapping("/generate")
	public String generate(@RequestParam("region") String region,
			@RequestParam("days") String days,
			@RequestParam(value = "style", required = false) String style,
			@RequestParam(value = "companion", required = false) String companion,
			Model model) {
		String planData = plannerService.generatePlan(region, days, style != null ? style : "",
				companion != null ? companion : "");
		model.addAttribute("planData", planData);
		model.addAttribute("region", region);
		model.addAttribute("days", days);
		model.addAttribute("style", style);
		model.addAttribute("companion", companion);
		model.addAttribute("title", days + " " + region + " 旅行");
		model.addAttribute("apiKey", apiKey);
		model.addAttribute("canEdit", true);
		model.addAttribute("isOwner", true);
		model.addAttribute("collaborators", new java.util.ArrayList<>());
		model.addAttribute("planner", null);
		return "planner/result";
	}

	@PostMapping("/save")
	public String save(@AuthenticationPrincipal UserDetails userDetails,
			@RequestParam("title") String title,
			@RequestParam("planData") String planData,
			RedirectAttributes redirectAttributes) {
		if (userDetails == null) {
			redirectAttributes.addFlashAttribute("errorMessage", "ログインが必要です。");
			return "redirect:/user/login";
		}
		UserDto user = userService.findByUsername(userDetails.getUsername()).orElseThrow();
		plannerService.savePlan(user.getUserId(), title, planData);
		redirectAttributes.addFlashAttribute("message", "プランを保存しました。");
		return "redirect:/planner/list";
	}

	@GetMapping("/list")
	public String list(@AuthenticationPrincipal UserDetails userDetails, Model model) {
		if (userDetails == null) {
			return "redirect:/user/login";
		}
		UserDto user = userService.findByUsername(userDetails.getUsername()).orElseThrow();
		List<PlannerDto> list = plannerService.getByUserId(user.getUserId());
		model.addAttribute("planners", list);
		return "planner/list";
	}

	@GetMapping("/detail")
	public String detail(@AuthenticationPrincipal UserDetails userDetails,
			@RequestParam("id") Long id,
			Model model,
			RedirectAttributes redirectAttributes) {
		PlannerDto planner = plannerService.getById(id);
		if (planner == null) {
			return "redirect:/planner/list";
		}

		Long userId = null;
		if (userDetails != null) {
			userId = userService.findByUsername(userDetails.getUsername()).map(UserDto::getUserId).orElse(null);
		}

		if (plannerService.canView(id, userId)) {
			model.addAttribute("planner", planner);
			model.addAttribute("planData", planner.getPlanData());
			model.addAttribute("title", planner.getTitle());
			model.addAttribute("apiKey", apiKey);
			model.addAttribute("canEdit", plannerService.canEdit(id, userId));
			model.addAttribute("collaborators", plannerService.getCollaborators(id));
			model.addAttribute("isOwner", userId != null && planner.getUserId().equals(userId));
			return "planner/result";
		}

		redirectAttributes.addFlashAttribute("errorMessage", "閲覧権限がありません。");
		return "redirect:/planner/list";
	}

	@PostMapping("/update")
	public String update(@AuthenticationPrincipal UserDetails userDetails,
			@RequestParam("plannerId") Long plannerId,
			@RequestParam("title") String title,
			@RequestParam("planData") String planData,
			RedirectAttributes redirectAttributes) {
		if (userDetails == null) {
			return "redirect:/user/login";
		}
		UserDto user = userService.findByUsername(userDetails.getUsername()).orElseThrow();
		if (!plannerService.canEdit(plannerId, user.getUserId())) {
			redirectAttributes.addFlashAttribute("errorMessage", "編集権限がありません。");
			return "redirect:/planner/list";
		}

		plannerService.updatePlan(plannerId, title, planData);
		redirectAttributes.addFlashAttribute("message", "プランを更新しました。");
		return "redirect:/planner/detail?id=" + plannerId;
	}

	@PostMapping("/delete")
	public String delete(@AuthenticationPrincipal UserDetails userDetails,
			@RequestParam("plannerId") Long plannerId,
			RedirectAttributes redirectAttributes) {
		if (userDetails == null) {
			return "redirect:/user/login";
		}
		PlannerDto planner = plannerService.getById(plannerId);
		UserDto user = userService.findByUsername(userDetails.getUsername()).orElseThrow();
		// Owner only for deletion
		if (planner == null || !planner.getUserId().equals(user.getUserId())) {
			redirectAttributes.addFlashAttribute("errorMessage", "削除権限がありません。");
			return "redirect:/planner/list";
		}

		plannerService.deletePlan(plannerId);
		redirectAttributes.addFlashAttribute("message", "プランを削除しました。");
		return "redirect:/planner/list";
	}

	// --- Share & Collaboration ---

	@PostMapping("/share/toggle")
	public String togglePublic(@AuthenticationPrincipal UserDetails userDetails,
			@RequestParam("plannerId") Long plannerId,
			@RequestParam("isPublic") boolean isPublic,
			RedirectAttributes redirectAttributes) {
		UserDto user = userService.findByUsername(userDetails.getUsername()).orElseThrow();
		PlannerDto planner = plannerService.getById(plannerId);
		if (planner == null || !planner.getUserId().equals(user.getUserId())) {
			return "redirect:/planner/list";
		}
		plannerService.updatePublicStatus(plannerId, isPublic);
		return "redirect:/planner/detail?id=" + plannerId;
	}

	@PostMapping("/invite")
	public String invite(@AuthenticationPrincipal UserDetails userDetails,
			@RequestParam("plannerId") Long plannerId,
			@RequestParam("inviteUsername") String inviteUsername,
			RedirectAttributes redirectAttributes) {
		UserDto user = userService.findByUsername(userDetails.getUsername()).orElseThrow();
		PlannerDto planner = plannerService.getById(plannerId);
		if (planner == null || !planner.getUserId().equals(user.getUserId())) {
			return "redirect:/planner/list";
		}
		plannerService.inviteCollaborator(plannerId, inviteUsername);
		redirectAttributes.addFlashAttribute("message", inviteUsername + "さんを招待しました。");
		return "redirect:/planner/detail?id=" + plannerId;
	}
}

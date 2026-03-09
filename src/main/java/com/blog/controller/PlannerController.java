package com.blog.controller;

import com.blog.dto.PlannerDto;
import com.blog.dto.UserDto;
import com.blog.service.PlannerService;
import com.blog.service.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/planner")
public class PlannerController {

	@Value("${maps.api.key}")
	private String googleMapsApiKey;

	private final PlannerService plannerService;
	private final UserService userService;
	private final ObjectMapper objectMapper = new ObjectMapper();

	public PlannerController(PlannerService plannerService, UserService userService) {
		this.plannerService = plannerService;
		this.userService = userService;
	}

	@GetMapping("/form")
	public String showForm(Model model) {
		model.addAttribute("apiKey", googleMapsApiKey);
		return "planner/form";
	}

	@PostMapping("/generate")
	public String generatePlan(
			@RequestParam("region") String region,
			@RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate,
			@RequestParam(value = "adults", defaultValue = "2") int adults,
			@RequestParam(value = "children", defaultValue = "0") int children,
			@RequestParam("style") String style,
			@RequestParam("companion") String companion,
			@RequestParam("accommodationName") String accommodationName,
			@RequestParam("accommodationAddress") String accommodationAddress,
			Model model, HttpSession session,
			@AuthenticationPrincipal UserDetails userDetails) {

		System.out.println("Generating plan for: " + region + " from " + startDate + " to " + endDate);
		System.out.println("Hotel: " + accommodationName + " (" + accommodationAddress + ")");

		String result = plannerService.generatePlan(region, startDate, endDate, adults, children, style, companion,
				accommodationName, accommodationAddress);

		if (result.contains("\"error\":")) {
			model.addAttribute("error", "AI 코스 생성 중 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.");
			model.addAttribute("apiKey", googleMapsApiKey);
			return "planner/form";
		}

		model.addAttribute("region", region);
		model.addAttribute("startDate", startDate);
		model.addAttribute("endDate", endDate);
		model.addAttribute("adults", adults);
		model.addAttribute("children", children);
		model.addAttribute("accommodation", accommodationName);
		model.addAttribute("planData", result);
		model.addAttribute("apiKey", googleMapsApiKey);
		model.addAttribute("title", region + " 旅行");

		// Enrich planData with passed parameters before saving/viewing
		try {
			JsonNode root = objectMapper.readTree(result);
			if (root instanceof ObjectNode) {
				ObjectNode rootNode = (ObjectNode) root;
				ObjectNode tripInfo = rootNode.has("trip_info") ? (ObjectNode) rootNode.get("trip_info")
						: rootNode.putObject("trip_info");
				tripInfo.put("start_date", startDate);
				tripInfo.put("end_date", endDate);
				tripInfo.put("adults", adults);
				tripInfo.put("children", children);
				tripInfo.put("accommodation", accommodationName);
				result = objectMapper.writeValueAsString(rootNode);
				model.addAttribute("planData", result); // Update model with enriched data
			}
		} catch (Exception e) {
			System.err.println("Failed to enrich plan data: " + e.getMessage());
		}

		// 로그인 상태라면 자동 저장 후 상세 페이지로 리다이렉트
		if (userDetails != null) {
			UserDto loginUser = userService.findByUsername(userDetails.getUsername()).orElseThrow();
			Long plannerId = plannerService.savePlan(loginUser.getUserId(), region + " 旅行", result);
			return "redirect:/planner/view/" + plannerId;
		}

		return "planner/result";
	}

	@PostMapping("/save")
	@ResponseBody
	public String savePlan(@RequestParam("title") String title, @RequestParam("planData") String planData,
			@AuthenticationPrincipal UserDetails userDetails) {
		if (userDetails == null)
			return "FAIL:Login Required";

		UserDto loginUser = userService.findByUsername(userDetails.getUsername()).orElseThrow();

		try {
			plannerService.savePlan(loginUser.getUserId(), title, planData);
			return "OK";
		} catch (Exception e) {
			return "FAIL:" + e.getMessage();
		}
	}

	@GetMapping("/list")
	public String listPlans(Model model, @AuthenticationPrincipal UserDetails userDetails) {
		if (userDetails == null)
			return "redirect:/user/login";

		UserDto loginUser = userService.findByUsername(userDetails.getUsername()).orElseThrow();

		List<PlannerDto> myPlans = plannerService.getByUserId(loginUser.getUserId());
		List<PlannerDto> collabPlans = plannerService.getCollaboratingPlanners(loginUser.getUserId());

		model.addAttribute("myPlans", myPlans);
		model.addAttribute("collabPlans", collabPlans);
		return "planner/list";
	}

	@GetMapping("/view/{id}")
	public String viewPlan(@PathVariable("id") Long id, Model model, @AuthenticationPrincipal UserDetails userDetails) {
		Long userId = null;
		if (userDetails != null) {
			userId = userService.findByUsername(userDetails.getUsername()).orElseThrow().getUserId();
		}

		if (!plannerService.canView(id, userId)) {
			return "redirect:/planner/list";
		}

		PlannerDto planner = plannerService.getById(id);
		model.addAttribute("planner", planner);
		model.addAttribute("planData", planner.getPlanData());
		model.addAttribute("title", planner.getTitle());
		model.addAttribute("apiKey", googleMapsApiKey);

		// result.html에서 사용하는 추가 속성들 (일단 기본값 혹은 데이터 파싱 필요할 수 있음)
		try {
			JsonNode root = objectMapper.readTree(planner.getPlanData());
			if (root.has("trip_info")) {
				JsonNode info = root.get("trip_info");
				model.addAttribute("startDate", info.path("start_date").asText());
				model.addAttribute("endDate", info.path("end_date").asText());
				model.addAttribute("adults", info.path("adults").asInt());
				model.addAttribute("children", info.path("children").asInt());
				model.addAttribute("accommodation", info.path("accommodation").asText());
			}
		} catch (Exception e) {
			System.err.println("Failed to parse plan data for view: " + e.getMessage());
		}

		model.addAttribute("canEdit", plannerService.canEdit(id, userId));
		model.addAttribute("isOwner", (userId != null && planner.getUserId().equals(userId)));
		model.addAttribute("collaborators", plannerService.getCollaborators(id));

		return "planner/result";
	}

	@PostMapping("/update")
	@ResponseBody
	public String updatePlan(@RequestParam("plannerId") Long plannerId, @RequestParam("title") String title,
			@RequestParam("planData") String planData,
			@AuthenticationPrincipal UserDetails userDetails) {
		if (userDetails == null) {
			return "FAIL:Login Required";
		}
		UserDto loginUser = userService.findByUsername(userDetails.getUsername()).orElseThrow();
		if (!plannerService.canEdit(plannerId, loginUser.getUserId())) {
			return "FAIL:Permission Denied";
		}

		try {
			plannerService.updatePlan(plannerId, title, planData);
			return "OK";
		} catch (Exception e) {
			return "FAIL:" + e.getMessage();
		}
	}

	@PostMapping("/delete/{id}")
	public String deletePlan(@PathVariable("id") Long id, @AuthenticationPrincipal UserDetails userDetails,
			RedirectAttributes rttr) {
		if (userDetails == null)
			return "redirect:/user/login";

		UserDto loginUser = userService.findByUsername(userDetails.getUsername()).orElseThrow();

		PlannerDto planner = plannerService.getById(id);
		if (planner == null || !planner.getUserId().equals(loginUser.getUserId())) {
			rttr.addFlashAttribute("error", "削除権限がありません。");
			return "redirect:/planner/list";
		}

		try {
			plannerService.deletePlan(id);
			rttr.addFlashAttribute("message", "プランが削除されました。");
		} catch (Exception e) {
			rttr.addFlashAttribute("error", "削除中にエラーが発生しました: " + e.getMessage());
		}
		return "redirect:/planner/list";
	}

	@PostMapping("/share/status")
	@ResponseBody
	public String updatePublicStatus(@RequestParam("plannerId") Long plannerId,
			@RequestParam("isPublic") boolean isPublic,
			@AuthenticationPrincipal UserDetails userDetails) {
		if (userDetails == null)
			return "FAIL:Login Required";

		UserDto loginUser = userService.findByUsername(userDetails.getUsername()).orElseThrow();
		PlannerDto planner = plannerService.getById(plannerId);
		if (planner == null || !planner.getUserId().equals(loginUser.getUserId())) {
			return "FAIL:Permission Denied";
		}

		plannerService.updatePublicStatus(plannerId, isPublic);
		return "OK";
	}

	@PostMapping("/share/invite")
	@ResponseBody
	public String inviteCollaborator(@RequestParam("plannerId") Long plannerId,
			@RequestParam("username") String username,
			@AuthenticationPrincipal UserDetails userDetails) {
		if (userDetails == null)
			return "FAIL:Login Required";

		UserDto loginUser = userService.findByUsername(userDetails.getUsername()).orElseThrow();
		PlannerDto planner = plannerService.getById(plannerId);
		if (planner == null || !planner.getUserId().equals(loginUser.getUserId())) {
			return "FAIL:Permission Denied";
		}

		plannerService.inviteCollaborator(plannerId, username);
		return "OK";
	}
}

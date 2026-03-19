package com.blog.controller;

import com.blog.dto.PlannerDto;
import com.blog.dto.UserDto;
import com.blog.dto.WishlistDto;
import com.blog.service.PlannerService;
import com.blog.service.UserService;
import com.blog.service.WishlistService;
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
	private final WishlistService wishlistService;
	private final com.blog.service.WeatherService weatherService;
	private final ObjectMapper objectMapper = new ObjectMapper();

	public PlannerController(PlannerService plannerService, UserService userService, WishlistService wishlistService,
			com.blog.service.WeatherService weatherService) {
		this.plannerService = plannerService;
		this.userService = userService;
		this.wishlistService = wishlistService;
		this.weatherService = weatherService;
	}

	@GetMapping("/form")
	public String showForm(Model model, @AuthenticationPrincipal UserDetails userDetails) {
		if (userDetails == null) {
			model.addAttribute("loginRequired", true);
		} else {
			UserDto loginUser = userService.findByUsername(userDetails.getUsername()).orElseThrow();
			List<WishlistDto> wishlist = wishlistService.getWishlistByUserId(loginUser.getUserId());
			model.addAttribute("wishlist", wishlist);
		}
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
			@RequestParam(value = "wishlistPlaces", required = false) List<String> wishlistPlaces,
			@RequestParam(value = "flightArrival", required = false) String flightArrival,
			@RequestParam(value = "flightDeparture", required = false) String flightDeparture,
			@RequestParam(value = "arrivalAirport", required = false) String arrivalAirport,
			@RequestParam(value = "departureAirport", required = false) String departureAirport,
			Model model, HttpSession session,
			@AuthenticationPrincipal UserDetails userDetails) {

		System.out.println("Generating plan for: " + region + " from " + startDate + " to " + endDate);
		System.out.println("Hotel: " + accommodationName + " (" + accommodationAddress + ")");
		System.out.println("Airports: arrival=" + arrivalAirport + ", departure=" + departureAirport);

		// 天気予報取得
		String cityKey = weatherService.getCityKey(region);
		List<com.blog.dto.WeatherDto> weatherForecast = weatherService.getWeatherForRange(cityKey, region, startDate,
				endDate);

		String result = plannerService.generatePlan(region, startDate, endDate, adults, children, style, companion,
				accommodationName, accommodationAddress, wishlistPlaces, flightArrival, flightDeparture,
				arrivalAirport, departureAirport, weatherForecast);

		model.addAttribute("region", region);
		model.addAttribute("startDate", startDate);
		model.addAttribute("endDate", endDate);
		model.addAttribute("adults", adults);
		model.addAttribute("children", children);
		model.addAttribute("style", style);
		model.addAttribute("companion", companion);
		model.addAttribute("accommodationName", accommodationName);
		model.addAttribute("accommodationAddress", accommodationAddress);
		model.addAttribute("apiKey", googleMapsApiKey);

		if (result.contains("\"error\":")) {
			String errorMsg = "AIコースの生成中にエラーが発生しました。しばらくしてからもう一度お試しください。";
			try {
				JsonNode root = objectMapper.readTree(result);
				if (root.has("error")) {
					errorMsg = root.get("error").asText();
				}
			} catch (Exception e) {
				System.err.println("Failed to parse error from Gemini: " + e.getMessage());
			}
			model.addAttribute("error", errorMsg);
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

		// planDataデータ強化
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
				model.addAttribute("planData", result); // 強化データモデル反映
			}
		} catch (Exception e) {
			System.err.println("Failed to enrich plan data: " + e.getMessage());
		}

		// ログイン時自動保存・リダイレクト
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

		// result.html追加属性設定
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

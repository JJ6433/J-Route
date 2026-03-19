package com.blog.controller;

import com.blog.dto.PlaceDto;
import com.blog.dto.ReviewDto;
import com.blog.dto.UserDto;
import com.blog.service.PlaceService;
import com.blog.service.ReviewService;
import com.blog.service.UserService;
import com.blog.service.WishlistService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 旅行先ユーザーページ
 * 一覧(フィルタ/ソート), 詳細(地図・レビュー・お気に入り表示)
 */
@Controller
@RequestMapping("/place")
public class PlaceController {

	private final PlaceService placeService;
	private final ReviewService reviewService;
	private final WishlistService wishlistService;
	private final UserService userService;

	@Value("${maps.api.key}")
	private String apiKey;

	public PlaceController(PlaceService placeService, ReviewService reviewService, WishlistService wishlistService,
			UserService userService) {
		this.placeService = placeService;
		this.reviewService = reviewService;
		this.wishlistService = wishlistService;
		this.userService = userService;
	}

	@GetMapping("/list")
	public String list(@RequestParam(value = "region", required = false) String region,
			@RequestParam(value = "category", required = false) String category,
			@RequestParam(value = "keyword", required = false) String keyword,
			@RequestParam(value = "sort", required = false, defaultValue = "latest") String sort,
			Model model) {
		List<PlaceDto> places = placeService.getList(region, category, keyword, sort);
		model.addAttribute("places", places);
		model.addAttribute("region", region);
		model.addAttribute("category", category);
		model.addAttribute("keyword", keyword);
		model.addAttribute("sort", sort);
		return "place/list";
	}

	@GetMapping("/detail")
	public String detail(@RequestParam("id") Long id,
			@AuthenticationPrincipal UserDetails userDetails,
			Model model) {
		PlaceDto place = placeService.getById(id);
		if (place == null) {
			return "redirect:/place/list";
		}
		List<ReviewDto> reviews = reviewService.getByPlaceId(id);
		double avgRating = reviewService.getAvgRating(id);
		int reviewCount = reviewService.getReviewCount(id);
		place.setAvgRating(avgRating);
		place.setReviewCount(reviewCount);

		Long userId = null;
		if (userDetails != null) {
			userId = userService.findByUsername(userDetails.getUsername()).map(UserDto::getUserId).orElse(null);
		}
		boolean wished = userId != null && wishlistService.isWished(userId, id);
		model.addAttribute("place", place);
		model.addAttribute("reviews", reviews);
		model.addAttribute("avgRating", avgRating);
		model.addAttribute("reviewCount", reviewCount);
		model.addAttribute("wished", wished);
		model.addAttribute("apiKey", apiKey);
		if (userId != null)
			model.addAttribute("currentUserId", userId);
		return "place/detail";
	}
}

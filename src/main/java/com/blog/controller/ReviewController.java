package com.blog.controller;

import com.blog.dto.ReviewDto;
import com.blog.dto.UserDto;
import com.blog.service.ReviewService;
import com.blog.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 리뷰 컨트롤러
 * 리뷰 등록/수정/삭제 (로그인 사용자)
 */
@Controller
@RequestMapping("/review")
public class ReviewController {

	private final ReviewService reviewService;
	private final UserService userService;

	public ReviewController(ReviewService reviewService, UserService userService) {
		this.reviewService = reviewService;
		this.userService = userService;
	}

	@PostMapping("/add")
	public String add(@AuthenticationPrincipal UserDetails userDetails,
			@RequestParam("placeId") Long placeId,
			@RequestParam("rating") int rating,
			@RequestParam("content") String content,
			RedirectAttributes redirectAttributes) {
		if (userDetails == null) {
			redirectAttributes.addFlashAttribute("errorMessage", "ログインが必要です。");
			return "redirect:/user/login";
		}
		UserDto user = userService.findByUsername(userDetails.getUsername()).orElseThrow();
		ReviewDto dto = new ReviewDto();
		dto.setUserId(user.getUserId());
		dto.setPlaceId(placeId);
		dto.setRating(rating);
		dto.setContent(content);
		try {
			reviewService.addReview(dto);
			redirectAttributes.addFlashAttribute("message", "レビューを投稿しました。");
		} catch (IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
		}
		return "redirect:/place/detail?id=" + placeId;
	}

	@PostMapping("/delete")
	public String delete(@AuthenticationPrincipal UserDetails userDetails,
			@RequestParam("reviewId") Long reviewId,
			@RequestParam("placeId") Long placeId,
			RedirectAttributes redirectAttributes) {
		if (userDetails == null)
			return "redirect:/user/login";
		UserDto user = userService.findByUsername(userDetails.getUsername()).orElseThrow();
		reviewService.deleteReview(reviewId, user.getUserId());
		redirectAttributes.addFlashAttribute("message", "レビューを削除しました。");
		return "redirect:/place/detail?id=" + placeId;
	}
}

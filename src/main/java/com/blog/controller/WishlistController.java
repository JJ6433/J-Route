package com.blog.controller;

import com.blog.dto.UserDto;
import com.blog.dto.WishlistDto;
import com.blog.service.UserService;
import com.blog.service.WishlistService;
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
 * お気に入りコントローラー
 * お気に入り追加/削除, マイページお気に入り一覧
 */
@Controller
@RequestMapping("/wishlist")
public class WishlistController {

	private final WishlistService wishlistService;
	private final UserService userService;

	public WishlistController(WishlistService wishlistService, UserService userService) {
		this.wishlistService = wishlistService;
		this.userService = userService;
	}

	@GetMapping("/list")
	public String list(@AuthenticationPrincipal UserDetails userDetails, Model model) {
		if (userDetails == null) {
			return "redirect:/user/login";
		}
		UserDto user = userService.findByUsername(userDetails.getUsername()).orElseThrow();
		List<WishlistDto> list = wishlistService.getWishlistByUserId(user.getUserId());
		model.addAttribute("wishlists", list);
		return "user/wishlist";
	}

	@PostMapping("/toggle")
	public String toggle(@AuthenticationPrincipal UserDetails userDetails,
			@RequestParam("placeId") Long placeId,
			@RequestParam(value = "redirect", required = false) String redirect,
			RedirectAttributes redirectAttributes) {
		if (userDetails == null) {
			return "redirect:/user/login";
		}
		UserDto user = userService.findByUsername(userDetails.getUsername()).orElseThrow();
		if (wishlistService.isWished(user.getUserId(), placeId)) {
			wishlistService.removeWish(user.getUserId(), placeId);
			redirectAttributes.addFlashAttribute("message", "お気に入りから削除しました。");
		} else {
			wishlistService.addWish(user.getUserId(), placeId);
			redirectAttributes.addFlashAttribute("message", "お気に入りに追加しました。");
		}
		if (redirect != null && !redirect.isEmpty()) {
			return "redirect:" + redirect;
		}
		return "redirect:/place/detail?id=" + placeId;
	}
}

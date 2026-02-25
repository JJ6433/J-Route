package com.blog.controller;

import com.blog.dto.UserDto;
import com.blog.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

/**
 * 회원 컨트롤러
 * 회원가입, 로그인 폼, 마이페이지, 정보 수정, 비밀번호 변경, 탈퇴
 */
@Controller
@RequestMapping("/user")
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping("/signup")
	public String signupForm(Model model) {
		model.addAttribute("userDto", new UserDto());
		return "user/signup";
	}

	@PostMapping("/signup")
	public String signup(@ModelAttribute UserDto userDto, Model model) {
		try {
			userService.registerUser(userDto);
		} catch (IllegalStateException e) {
			model.addAttribute("userDto", userDto);
			model.addAttribute("errorMessage", e.getMessage());
			return "user/signup";
		} catch (Exception e) {
			// DB 연결 실패 등 기타 오류 시 메시지 표시
			model.addAttribute("userDto", userDto);
			String msg = e.getMessage() != null ? e.getMessage() : "登録に失敗しました。データベースの接続を確認してください。";
			model.addAttribute("errorMessage", msg);
			return "user/signup";
		}
		return "redirect:/user/login";
	}

	@GetMapping("/login")
	public String loginForm(@RequestParam(value = "error", required = false) String error, Model model) {
		if ("true".equals(error)) {
			model.addAttribute("errorMessage", "ユーザーIDまたはパスワードが正しくありません。");
		}
		return "user/login";
	}

	/** 마이페이지: 로그인 사용자만 */
	@GetMapping("/mypage")
	public String mypage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
		if (userDetails == null) {
			return "redirect:/user/login";
		}
		Optional<UserDto> user = userService.findByUsername(userDetails.getUsername());
		user.orElseThrow(() -> new IllegalArgumentException("会員情報が見つかりません。"));
		model.addAttribute("user", user.get());
		return "user/mypage";
	}

	@PostMapping("/mypage/update")
	public String updateProfile(@AuthenticationPrincipal UserDetails userDetails,
			@RequestParam("nickname") String nickname,
			RedirectAttributes redirectAttributes) {
		UserDto user = userService.findByUsername(userDetails.getUsername()).orElseThrow();
		userService.updateUser(user.getUserId(), nickname);
		redirectAttributes.addFlashAttribute("message", "プロフィールを更新しました。");
		return "redirect:/user/mypage";
	}

	@PostMapping("/mypage/password")
	public String changePassword(@AuthenticationPrincipal UserDetails userDetails,
			@RequestParam("currentPassword") String currentPassword,
			@RequestParam("newPassword") String newPassword,
			RedirectAttributes redirectAttributes) {
		UserDto user = userService.findByUsername(userDetails.getUsername()).orElseThrow();
		try {
			userService.changePassword(user.getUserId(), currentPassword, newPassword);
			redirectAttributes.addFlashAttribute("message", "パスワードを変更しました。");
		} catch (IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
		}
		return "redirect:/user/mypage";
	}

	@PostMapping("/mypage/withdraw")
	public String withdraw(@AuthenticationPrincipal UserDetails userDetails) {
		UserDto user = userService.findByUsername(userDetails.getUsername()).orElseThrow();
		userService.withdraw(user.getUserId());
		return "redirect:/logout";
	}
}

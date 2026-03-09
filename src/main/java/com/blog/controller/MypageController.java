package com.blog.controller;

import com.blog.dto.ReservationDto; 
import com.blog.dto.ReviewDto;
import com.blog.dto.UserDto;
import com.blog.dto.WishlistDto;
import com.blog.service.ReservationService; 
import com.blog.service.ReviewService;
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

@Controller
@RequestMapping("/mypage")
public class MypageController {

    private final UserService userService;
    private final WishlistService wishlistService;
    private final ReviewService reviewService;
    private final ReservationService reservationService; 

    public MypageController(UserService userService, WishlistService wishlistService, 
                            ReviewService reviewService, ReservationService reservationService) {
        this.userService = userService;
        this.wishlistService = wishlistService;
        this.reviewService = reviewService;
        this.reservationService = reservationService;
    }

    private UserDto getAuthenticatedUser(UserDetails userDetails) {
        if (userDetails == null)
            return null;
        return userService.findByUsername(userDetails.getUsername()).orElse(null);
    }

    @GetMapping
    public String index(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        UserDto user = getAuthenticatedUser(userDetails);
        if (user == null)
            return "redirect:/login";

        List<WishlistDto> wishlists = wishlistService.getWishlistByUserId(user.getUserId());
        List<ReviewDto> reviews = reviewService.getReviewsByUserId(user.getUserId());
        int reviewCount = reviewService.getReviewCountByUserId(user.getUserId());

        // 💡 핵심 변경: getUsername() -> getUserId() 로 원상복구! (숫자 PK로 조회)
        int reservationCount = reservationService.getReservationCount(user.getUserId());
        List<ReservationDto> recentReservations = reservationService.getRecentReservations(user.getUserId());

        model.addAttribute("user", user);
        model.addAttribute("wishCount", wishlists.size());
        model.addAttribute("reviewCount", reviewCount);
        model.addAttribute("recentReviews", reviews.size() > 3 ? reviews.subList(0, 3) : reviews);
        
        model.addAttribute("reservationCount", reservationCount);
        model.addAttribute("recentReservations", recentReservations);

        return "user/mypage";
    }

    @GetMapping("/wishlist")
    public String wishlist(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        UserDto user = getAuthenticatedUser(userDetails);
        if (user == null)
            return "redirect:/login";

        model.addAttribute("wishlists", wishlistService.getWishlistByUserId(user.getUserId()));
        return "user/wishlist";
    }

    @GetMapping("/reviews")
    public String reviews(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        UserDto user = getAuthenticatedUser(userDetails);
        if (user == null)
            return "redirect:/login";

        model.addAttribute("reviews", reviewService.getReviewsByUserId(user.getUserId()));
        return "user/reviews";
    }

    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        UserDto user = getAuthenticatedUser(userDetails);
        if (user == null)
            return "redirect:/login";

        model.addAttribute("user", user);
        return "user/profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("nickname") String nickname,
            RedirectAttributes ra) {
        UserDto user = getAuthenticatedUser(userDetails);
        if (user == null)
            return "redirect:/login";

        try {
            userService.updateUser(user.getUserId(), nickname);
            ra.addFlashAttribute("message", "プロフィールを更新しました。");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "更新に失敗しました: " + e.getMessage());
        }
        return "redirect:/mypage/profile";
    }

    @PostMapping("/profile/password")
    public String updatePassword(@AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("currentPassword") String currentPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            RedirectAttributes ra) {
        UserDto user = getAuthenticatedUser(userDetails);
        if (user == null)
            return "redirect:/login";

        if (!newPassword.equals(confirmPassword)) {
            ra.addFlashAttribute("errorMessage", "新しいパスワードと確認用パスワードが一致しません。");
            return "redirect:/mypage/profile";
        }

        try {
            userService.changePassword(user.getUserId(), currentPassword, newPassword);
            ra.addFlashAttribute("message", "パスワードを更新しました。");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/mypage/profile";
    }
    
    // 예약 내역 전체 보기 페이지
    @GetMapping("/reservations")
    public String reservations(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        UserDto user = getAuthenticatedUser(userDetails);
        if (user == null)
            return "redirect:/login";

        //  핵심 변경: getUsername() -> getUserId() 로 원상복구! (숫자 PK로 조회)
        List<ReservationDto> reservations = reservationService.getAllReservations(user.getUserId());
        model.addAttribute("reservations", reservations);
        
        return "user/reservations"; 
    }
}
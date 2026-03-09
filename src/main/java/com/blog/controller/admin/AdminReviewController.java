package com.blog.controller.admin;

import com.blog.service.ReviewService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/review")
@PreAuthorize("hasRole('ADMIN')")
public class AdminReviewController {

    private final ReviewService reviewService;

    public AdminReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    public String reviewList(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "rating", required = false) Integer rating,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            Model model) {

        model.addAttribute("reviews", reviewService.findWithFilters(keyword, rating, startDate, endDate));
        model.addAttribute("keyword", keyword);
        model.addAttribute("rating", rating);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        return "admin/reviews";
    }

    @PostMapping("/delete/{id}")
    public String deleteReview(@PathVariable("id") Long reviewId, RedirectAttributes redirectAttributes) {
        try {
            reviewService.deleteReviewByAdmin(reviewId);
            redirectAttributes.addFlashAttribute("message", "レビューを削除しました。");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "削除に失敗しました: " + e.getMessage());
        }
        return "redirect:/admin/review";
    }
}

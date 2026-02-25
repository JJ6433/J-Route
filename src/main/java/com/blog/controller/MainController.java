package com.blog.controller;

import com.blog.dto.PlaceDto;
import com.blog.service.NoticeService;
import com.blog.service.PlaceService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

/**
 * 메인 페이지 컨트롤러
 * "/" 요청 시 추천(인기) 여행지 카드 표시
 */
@Controller
public class MainController {

	private final PlaceService placeService;
	private final NoticeService noticeService;

	public MainController(PlaceService placeService, NoticeService noticeService) {
		this.placeService = placeService;
		this.noticeService = noticeService;
	}

	@GetMapping("/")
	public String index(Model model) {
		// 인기 여행지 상위 9건 (TOP 3는 별도 표시, 나머지는 추천 목록)
		List<PlaceDto> allPlaces = placeService.getTopPlaces(9);

		if (allPlaces == null || allPlaces.isEmpty()) {
			allPlaces = placeService.getAllPlaces();
		}

		List<PlaceDto> top3 = new ArrayList<>();
		List<PlaceDto> recommendations = new ArrayList<>();

		if (allPlaces != null) {
			for (int i = 0; i < allPlaces.size(); i++) {
				if (i < 3) {
					top3.add(allPlaces.get(i));
				} else {
					recommendations.add(allPlaces.get(i));
				}
			}
		}

		model.addAttribute("topPlaces", top3);
		model.addAttribute("recommendedPlaces", recommendations);
		model.addAttribute("notices", noticeService.getActiveNotices());
		return "index";
	}
}

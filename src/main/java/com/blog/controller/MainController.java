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
 * メインページコントローラー
 * "/" リクエスト時に推奨(人気)旅行先カードを表示
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
		// 人気旅行先TOP9照会
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

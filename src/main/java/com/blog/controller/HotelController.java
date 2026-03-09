package com.blog.controller;

import com.blog.service.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HotelController {

    @Autowired
    private HotelService hotelService;

    // 1. 숙소 검색 메인 페이지 이동
    @GetMapping("/trip/hotels")
    public String showHotelSearchForm(Model model) {
        model.addAttribute("activeMenu", "hotels"); 
        return "hotel/search"; 
    }

    // 2. 숙소 검색 결과 페이지 이동 (추가된 부분)
    @GetMapping("/hotel-search-page")
    public String searchHotels(
            @RequestParam("destId") String destId,
            @RequestParam("checkinDate") String checkinDate,
            @RequestParam("checkoutDate") String checkoutDate,
            @RequestParam("adults") String adults,
            Model model) {

        // Service를 호출하여 숙소 JSON 데이터를 가져옵니다.
        String hotelData = hotelService.searchHotels(destId, checkinDate, checkoutDate, adults);

        // 모델에 담아서 결과 페이지로 전송
        model.addAttribute("hotels", hotelData);
        model.addAttribute("activeMenu", "hotels");

        return "hotel/hotel-results"; // 결과 출력 HTML로 이동
    }
}
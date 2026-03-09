package com.blog.controller;

import com.blog.service.FlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class FlightController {

    @Autowired
    private FlightService flightService;

    /**
     * 1. 항공권 검색 페이지 이동
     * 사이드바의 '항공권 예약(航空券予約)' 메뉴를 클릭했을 때 호출됩니다.
     */
    @GetMapping("/trip/flights")
    public String showSearchForm(Model model) {
        model.addAttribute("activeMenu", "flights"); // 사이드바 메뉴 활성화 상태 유지
        return "flight/search"; // templates/flight/search.html 실행
    }

    /**
     * 2. 항공권 검색 결과 페이지 이동
     
     */
    @GetMapping("/search-page")
    public String showSearchPage(@RequestParam("fromId") String fromId, 
                                 @RequestParam("toId") String toId, 
                                 @RequestParam("departDate") String departDate, 
                                 @RequestParam(value = "returnDate", required = false) String returnDate, // returnDate 추가
                                 Model model) {
        
        // Service 호출 시 returnDate를 함께 넘깁니다.
        String flightData = flightService.searchFlights(fromId, toId, departDate, returnDate);
        
        model.addAttribute("flights", flightData); 
        model.addAttribute("activeMenu", "flights");
        
        return "flight/flight-results"; 
    }

    // --- 아래는 기존 테스트용 JSON 데이터 API 입니다 (필요 시 유지) ---

    /**
     * 기존 데이터 API (JSON 결과 확인용)
     */
    @ResponseBody 
    @GetMapping("/api/flights/search")
    public String searchFlightsApi(
            @RequestParam("fromId") String fromId,
            @RequestParam("toId") String toId,
            @RequestParam("departDate") String departDate,
            @RequestParam(value = "returnDate", required = false) String returnDate) { // 추가
        
        return flightService.searchFlights(fromId, toId, departDate, returnDate); 
    }
    
    /**
     * 기존 위치 검색 API (JSON 결과 확인용)
     */
    @ResponseBody
    @GetMapping("/api/flights/location")
    public String getLocation(@RequestParam("query") String query) {
        return flightService.searchLocation(query);
    }
}
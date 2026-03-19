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
     * 1. 航空券検索ページ移動
     * サイドバーの「航空券予約」メニューをクリックしたときに呼び出されます。
     */
    @GetMapping("/trip/flights")
    public String showSearchForm(Model model) {
        model.addAttribute("activeMenu", "flights"); // サイドバーメニュー活性化維持
        return "flight/search"; // search.html実行
    }

    /**
     * 2. 航空券検索結果ページ移動
     */
    @GetMapping("/search-page")
    public String showSearchPage(@RequestParam("fromId") String fromId, 
                                 @RequestParam("toId") String toId, 
                                 @RequestParam("departDate") String departDate, 
                                 @RequestParam(value = "returnDate", required = false) String returnDate, // returnDate追加
                                 Model model) {
        
        // Service呼出時returnDate伝達
        String flightData = flightService.searchFlights(fromId, toId, departDate, returnDate);
        
        model.addAttribute("flights", flightData); 
        model.addAttribute("activeMenu", "flights");
        
        return "flight/flight-results"; 
    }

    // --- 既存テスト用JSON API (必要時維持) ---

    /**
     * 既存データ API (JSON結果確認用)
     */
    @ResponseBody 
    @GetMapping("/api/flights/search")
    public String searchFlightsApi(
            @RequestParam("fromId") String fromId,
            @RequestParam("toId") String toId,
            @RequestParam("departDate") String departDate,
            @RequestParam(value = "returnDate", required = false) String returnDate) { // 追加
        
        return flightService.searchFlights(fromId, toId, departDate, returnDate); 
    }
    
    /**
     * 既存位置検索 API (JSON結果確認用)
     */
    @ResponseBody
    @GetMapping("/api/flights/location")
    public String getLocation(@RequestParam("query") String query) {
        return flightService.searchLocation(query);
    }
}
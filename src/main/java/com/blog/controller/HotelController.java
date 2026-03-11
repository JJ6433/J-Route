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

    private final com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();

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

    // 3. AJAX 전용 숙소 검색 (도시 이름으로 검색)
    @GetMapping("/api/hotels/search")
    @org.springframework.web.bind.annotation.ResponseBody
    public String apiSearchHotels(@RequestParam("city") String city) {
        try {
            String searchQuery = city;
            // 영어 검색이 더 인식률이 좋을 수 있으므로 주요 도시는 영어로 변환하여 검색 시도
            if ("東京".equals(city)) searchQuery = "Tokyo";
            else if ("大阪".equals(city)) searchQuery = "Osaka";
            else if ("福岡".equals(city)) searchQuery = "Fukuoka";
            else if ("札幌".equals(city)) searchQuery = "Sapporo";
            else if ("沖縄".equals(city)) searchQuery = "Okinawa";
            else if ("名古屋".equals(city)) searchQuery = "Nagoya";
            else if ("静岡".equals(city)) searchQuery = "Shizuoka";
            else if ("広島".equals(city)) searchQuery = "Hiroshima";
            else if ("京都".equals(city)) searchQuery = "Kyoto";

            System.out.println("Starting hotel search for query: " + searchQuery + " (Original: " + city + ")");
            // 1. 도시 이름으로 destId 찾기
            String destData = hotelService.searchDestination(searchQuery);
            com.fasterxml.jackson.databind.JsonNode root = mapper.readTree(destData);
            
            if (root.has("data") && root.get("data").isArray() && root.get("data").size() > 0) {
                com.fasterxml.jackson.databind.JsonNode firstDest = root.get("data").get(0);
                String destId = firstDest.get("dest_id").asText();
                String searchType = firstDest.has("search_type") ? firstDest.get("search_type").asText() : "CITY";
                
                System.out.println("Found destination: " + destId + " with type: " + searchType);
                
                // 2. 기본 날짜 및 인원으로 호텔 검색 (예: 7일 후 기준 1박, 2명)
                java.time.LocalDate today = java.time.LocalDate.now();
                String checkin = today.plusDays(7).toString();
                String checkout = today.plusDays(8).toString();
                
                return hotelService.searchHotels(destId, searchType, checkin, checkout, "2");
            }
            // dest_id를 못 찾은 경우 혹은 에러 메시지가 포함된 경우
            if (root.has("error")) {
                return destData; // 전달받은 에러 JSON 그대로 반환
            }
            
            System.out.println("No destination found for city: " + city);
            com.fasterxml.jackson.databind.node.ObjectNode errorNode = mapper.createObjectNode();
            errorNode.put("error", "No destination found for " + city);
            return mapper.writeValueAsString(errorNode);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                com.fasterxml.jackson.databind.node.ObjectNode errorNode = mapper.createObjectNode();
                errorNode.put("error", "Search Error: " + e.getMessage());
                return mapper.writeValueAsString(errorNode);
            } catch (Exception ex) {
                return "{\"error\": \"Fatal Search Error\"}";
            }
        }
    }
    
    // 4. 숙소 상세 페이지 이동
    @GetMapping("/hotel/detail")
    public String showHotelDetail(
            @RequestParam(value = "id", required = false) String id,
            @RequestParam(value = "name", required = false, defaultValue = "選択した宿泊施設") String name,
            @RequestParam(value = "price", required = false, defaultValue = "-") String price,
            @RequestParam(value = "photoUrl", required = false, defaultValue = "https://placehold.co/900x400/e9ecef/6c757d?text=No+Image") String photoUrl,
            @RequestParam(value = "checkinDate", required = false, defaultValue = "未定") String checkinDate,
            @RequestParam(value = "checkoutDate", required = false, defaultValue = "未定") String checkoutDate,
            @RequestParam(value = "adults", required = false, defaultValue = "2") String adults,
            Model model) {

        model.addAttribute("hotelId", id);
        model.addAttribute("hotelName", name);
        model.addAttribute("price", price);
        model.addAttribute("photoUrl", photoUrl);
        model.addAttribute("checkinDate", checkinDate);
        model.addAttribute("checkoutDate", checkoutDate);
        model.addAttribute("adults", adults);

        return "hotel/hotel-detail"; // 도착지 HTML 이름
    }
}
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
    public String apiSearchHotels(
            @RequestParam("city") String city,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "checkin", required = false) String checkinParam,
            @RequestParam(value = "checkout", required = false) String checkoutParam,
            @RequestParam(value = "adults", required = false) String adultsParam) {
        try {
            String searchQuery = city;
            // 영어 검색이 더 인식률이 좋을 수 있으므로 주요 도시는 영어로 변환하여 검색 시도
            if ("東京".equals(city))
                searchQuery = "Tokyo";
            else if ("大阪".equals(city))
                searchQuery = "Osaka";
            else if ("福岡".equals(city))
                searchQuery = "Fukuoka";
            else if ("札幌".equals(city))
                searchQuery = "Sapporo";
            else if ("沖縄".equals(city))
                searchQuery = "Okinawa";
            else if ("名古屋".equals(city))
                searchQuery = "Nagoya";
            else if ("静岡".equals(city))
                searchQuery = "Shizuoka";
            else if ("広島".equals(city))
                searchQuery = "Hiroshima";
            else if ("京都".equals(city))
                searchQuery = "Kyoto";

            // [개선] 키워드가 있으면 도시 이름 앞에 붙여서 구체적으로 검색
            if (keyword != null && !keyword.trim().isEmpty()) {
                searchQuery = keyword + " " + searchQuery;
            }

            System.out.println("Starting hotel search for query: " + searchQuery + " (Original City: " + city
                    + ", Keyword: " + keyword + ")");
            // 1. 도시 이름으로 destId 찾기
            String destData = hotelService.searchDestination(searchQuery);
            com.fasterxml.jackson.databind.JsonNode root = mapper.readTree(destData);

            // [수정] 만약 키워드 검색 결과가 없으면, 키워드만으로 재시도 (일본 한정)
            if ((!root.has("data") || root.get("data").size() == 0) && keyword != null && !keyword.trim().isEmpty()) {
                System.out.println(
                        "[Search] No results for combined query: " + searchQuery + ". Trying keyword + Japan: "
                                + keyword);
                destData = hotelService.searchDestination(keyword + " Japan");
                root = mapper.readTree(destData);
            }

            if (root.has("data") && root.get("data").isArray() && root.get("data").size() > 0) {
                com.fasterxml.jackson.databind.JsonNode firstDest = null;
                String cityJp = city;
                // [수정] searchQuery가 "InterContinental Tokyo"인 경우 cityEn을 정확히 추출 (마지막 단어가 대개
                // 도시/국가)
                String[] queryParts = searchQuery.split(" ");
                String cityEn = queryParts[queryParts.length - 1];
                String lowerCityJp = cityJp.toLowerCase();
                String lowerCityEn = cityEn.toLowerCase();
                String lowerKeyword = (keyword != null) ? keyword.toLowerCase() : "";

                // 1순위: 키워드가 포함된 PROPERTY (정확한 호텔 매칭)
                if (!lowerKeyword.isEmpty()) {
                    for (com.fasterxml.jackson.databind.JsonNode dest : root.get("data")) {
                        String type = dest.path("search_type").asText("");
                        String name = dest.path("name").asText("").toLowerCase();
                        String label = dest.path("label").asText("").toLowerCase();

                        if (("PROPERTY".equals(type) || "hotel".equals(type.toLowerCase())) &&
                                (name.contains(lowerKeyword) || label.contains(lowerKeyword))) {
                            // [수정] 키워드 일치 시 도시 이름이 없더라도 우선 채택 (이미 searchQuery에 포함되었음)
                            firstDest = dest;
                            break;
                        }
                    }
                }

                // 2순위: 도시와 일본 조건이 맞는 결과 찾기
                if (firstDest == null) {
                    for (com.fasterxml.jackson.databind.JsonNode dest : root.get("data")) {
                        String label = dest.path("label").asText("").toLowerCase();
                        String name = dest.path("name").asText("").toLowerCase();
                        boolean isJapan = label.contains("japan") || label.contains("日本") || label.isEmpty();
                        boolean isTargetCity = label.contains(lowerCityJp) || label.contains(lowerCityEn) ||
                                name.contains(lowerCityJp) || name.contains(lowerCityEn);

                        if (isJapan && isTargetCity) {
                            firstDest = dest;
                            break;
                        }
                    }
                }

                // 마땅한 결과가 없으면 첫 번째 결과 사용
                if (firstDest == null)
                    firstDest = root.get("data").get(0);

                String destId = firstDest.get("dest_id").asText();
                String searchType = firstDest.has("search_type") ? firstDest.get("search_type").asText() : "CITY";

                System.out.println("[Search] Selected destination: " + destId + " (" + firstDest.get("name").asText()
                        + ") for city: " + city);

                // 2. 전달받은 조건이 있으면 사용, 없으면 기본 날짜 적용
                java.time.LocalDate today = java.time.LocalDate.now();
                String checkin = (checkinParam != null && !checkinParam.isEmpty() && !"undefined".equals(checkinParam))
                        ? checkinParam
                        : today.plusDays(7).toString();
                String checkout = (checkoutParam != null && !checkoutParam.isEmpty()
                        && !"undefined".equals(checkoutParam)) ? checkoutParam : today.plusDays(8).toString();
                String adults = (adultsParam != null && !adultsParam.isEmpty() && !"undefined".equals(adultsParam))
                        ? adultsParam
                        : "2";

                String hotelsData = hotelService.searchHotels(destId, searchType, checkin, checkout, adults);

                // [추가] 결과 리스트에서도 주소 및 도시 필터링 (선택된 도시 숙소만 허용)
                try {
                    com.fasterxml.jackson.databind.JsonNode hotelsRoot = mapper.readTree(hotelsData);
                    if (hotelsRoot.has("data") && hotelsRoot.get("data").has("hotels")) {
                        com.fasterxml.jackson.databind.node.ObjectNode dataNode = (com.fasterxml.jackson.databind.node.ObjectNode) hotelsRoot
                                .get("data");
                        com.fasterxml.jackson.databind.node.ArrayNode hotelsArray = (com.fasterxml.jackson.databind.node.ArrayNode) dataNode
                                .get("hotels");
                        com.fasterxml.jackson.databind.node.ArrayNode filteredArray = mapper.createArrayNode();

                        // 상단에 정의된 변수 재사용 (lowerKeyword, lowerCityJp, lowerCityEn)

                        for (com.fasterxml.jackson.databind.JsonNode hotel : hotelsArray) {
                            String hotelName = hotel.path("property").path("name").asText("").toLowerCase();
                            String address = hotel.path("property").path("address").asText("").toLowerCase();
                            String wishlistName = hotel.path("property").path("wishlistName").asText("").toLowerCase();

                            // 1. 일본 내 숙소인지 확인
                            boolean isJapan = address.contains("japan") || address.contains("日本");

                            // 2. 선택한 도시 내 숙소인지 확인 (도시 이름 포함 여부)
                            boolean isTargetCity = address.contains(lowerCityJp) || address.contains(lowerCityEn) ||
                                    wishlistName.contains(lowerCityJp) || wishlistName.contains(lowerCityEn);

                            // 3. 키워드가 있는 경우 키워드 포함 확인
                            boolean matchesKeyword = lowerKeyword.isEmpty() || hotelName.contains(lowerKeyword);

                            if (isJapan && isTargetCity && matchesKeyword) {
                                filteredArray.add(hotel);
                            }
                        }

                        // 필터링된 결과가 있으면 교체
                        if (filteredArray.size() > 0) {
                            dataNode.set("hotels", filteredArray);
                            return mapper.writeValueAsString(hotelsRoot);
                        } else if (!lowerKeyword.isEmpty()) {
                            // 키워드 검색 결과가 없는 경우
                            com.fasterxml.jackson.databind.node.ObjectNode errorNode = mapper.createObjectNode();
                            errorNode.put("error", "日本国内で「" + keyword + "」に一致する宿泊施設が見つかりませんでした。");
                            return mapper.writeValueAsString(errorNode);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("[Search] Filtering error: " + e.getMessage());
                    // 필터링 중 에러 나면 그냥 원본 데이터 반환 (차선책)
                }

                return hotelsData;
            }

            // dest_id를 못 찾은 경우
            if (root.has("error")) {
                return destData;
            }

            System.out.println("[Search] No destination found for: " + (keyword != null ? keyword : city));
            com.fasterxml.jackson.databind.node.ObjectNode errorNode = mapper.createObjectNode();
            String errorMsg = (keyword != null)
                    ? "「" + keyword + "」は見つかりませんでした。"
                    : "「" + city + "」の宿泊情報が見つかりませんでした。";
            errorNode.put("error", errorMsg);
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
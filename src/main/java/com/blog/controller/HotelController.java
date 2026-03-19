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

    // 宿泊検索・メインページ移動
    @GetMapping("/trip/hotels")
    public String showHotelSearchForm(Model model) {
        model.addAttribute("activeMenu", "hotels");
        return "hotel/search";
    }

    // 宿泊検索・結果ページ移動
    @GetMapping("/hotel-search-page")
    public String searchHotels(
            @RequestParam("destId") String destId,
            @RequestParam("checkinDate") String checkinDate,
            @RequestParam("checkoutDate") String checkoutDate,
            @RequestParam("adults") String adults,
            Model model) {

        // Service呼出(宿泊JSONデータ取得)
        String hotelData = hotelService.searchHotels(destId, checkinDate, checkoutDate, adults);

        // 結果ページ転送用モデル設定
        model.addAttribute("hotels", hotelData);
        model.addAttribute("activeMenu", "hotels");

        return "hotel/hotel-results"; // 結果表示HTML遷移
    }

    // AJAX専用・宿泊検索（都市名基準）
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
            // 精度向上のため主要都市を英語変換し検索
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

            // キーワード付加詳細検索
            if (keyword != null && !keyword.trim().isEmpty()) {
                searchQuery = keyword + " " + searchQuery;
            }

            System.out.println("Starting hotel search for query: " + searchQuery + " (Original City: " + city
                    + ", Keyword: " + keyword + ")");
            // 都市名基盤destId検索
            String destData = hotelService.searchDestination(searchQuery);
            com.fasterxml.jackson.databind.JsonNode root = mapper.readTree(destData);

            // 検索失敗時キーワード単独再検索
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
                // cityEn正確抽出
                String[] queryParts = searchQuery.split(" ");
                String cityEn = queryParts[queryParts.length - 1];
                String lowerCityJp = cityJp.toLowerCase();
                String lowerCityEn = cityEn.toLowerCase();
                String lowerKeyword = (keyword != null) ? keyword.toLowerCase() : "";

                // 1順位: キーワードマッチ
                if (!lowerKeyword.isEmpty()) {
                    for (com.fasterxml.jackson.databind.JsonNode dest : root.get("data")) {
                        String type = dest.path("search_type").asText("");
                        String name = dest.path("name").asText("").toLowerCase();
                        String label = dest.path("label").asText("").toLowerCase();

                        if (("PROPERTY".equals(type) || "hotel".equals(type.toLowerCase())) &&
                                (name.contains(lowerKeyword) || label.contains(lowerKeyword))) {
                            // [修正] キーワード一致時都市名なくても優先採択
                            firstDest = dest;
                            break;
                        }
                    }
                }

                // 2順位: 都市と日本条件合致結果検索
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

                // 適当な結果がない場合最初の結果使用
                if (firstDest == null)
                    firstDest = root.get("data").get(0);

                String destId = firstDest.get("dest_id").asText();
                String searchType = firstDest.has("search_type") ? firstDest.get("search_type").asText() : "CITY";

                System.out.println("[Search] Selected destination: " + destId + " (" + firstDest.get("name").asText()
                        + ") for city: " + city);

                // 2. 伝達条件有無による日付適用
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

                // [追加] 結果リスト住所/都市フィルタリング
                try {
                    com.fasterxml.jackson.databind.JsonNode hotelsRoot = mapper.readTree(hotelsData);
                    if (hotelsRoot.has("data") && hotelsRoot.get("data").has("hotels")) {
                        com.fasterxml.jackson.databind.node.ObjectNode dataNode = (com.fasterxml.jackson.databind.node.ObjectNode) hotelsRoot
                                .get("data");
                        com.fasterxml.jackson.databind.node.ArrayNode hotelsArray = (com.fasterxml.jackson.databind.node.ArrayNode) dataNode
                                .get("hotels");
                        com.fasterxml.jackson.databind.node.ArrayNode filteredArray = mapper.createArrayNode();

                        // 定義済み変数再利用

                        for (com.fasterxml.jackson.databind.JsonNode hotel : hotelsArray) {
                            String hotelName = hotel.path("property").path("name").asText("").toLowerCase();
                            String address = hotel.path("property").path("address").asText("").toLowerCase();
                            String wishlistName = hotel.path("property").path("wishlistName").asText("").toLowerCase();

                            // 1. 日本国内確認
                            boolean isJapan = address.contains("japan") || address.contains("日本");

                            // 2. 選択都市内確認
                            boolean isTargetCity = address.contains(lowerCityJp) || address.contains(lowerCityEn) ||
                                    wishlistName.contains(lowerCityJp) || wishlistName.contains(lowerCityEn);

                            // 3. キーワード包含確認
                            boolean matchesKeyword = lowerKeyword.isEmpty() || hotelName.contains(lowerKeyword);

                            if (isJapan && isTargetCity && matchesKeyword) {
                                filteredArray.add(hotel);
                            }
                        }

                        // フィルタリング結果交替
                        if (filteredArray.size() > 0) {
                            dataNode.set("hotels", filteredArray);
                            return mapper.writeValueAsString(hotelsRoot);
                        } else if (!lowerKeyword.isEmpty()) {
                            // キーワード検索結果なし
                            com.fasterxml.jackson.databind.node.ObjectNode errorNode = mapper.createObjectNode();
                            errorNode.put("error", "日本国内で「" + keyword + "」に一致する宿泊施設が見つかりませんでした。");
                            return mapper.writeValueAsString(errorNode);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("[Search] Filtering error: " + e.getMessage());
                    // フィルタリングエラー時原本データ返却
                }

                return hotelsData;
            }

            // dest_id検索失敗時
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

    // 4. 宿泊詳細ページ移動
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

        return "hotel/hotel-detail"; // 到着先HTML名
    }
}
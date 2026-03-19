package com.blog.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class HotelService {

    @Value("${rapidapi.key}")
    private String apiKey;

    @Value("${rapidapi.host}")
    private String apiHost;

    /**
     * 都市名で dest_id 検索 (Booking.com)
     */
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();

    /**
     * 都市名で dest_id 検索 (Booking.com)
     */
    public String searchDestination(String query) {
        // APIキーなし/テスト時Mockデータ返却
        if (apiKey == null || apiKey.isEmpty() || apiKey.equals("YOUR_API_KEY")) {
            return getMockDestination(query);
        }

        RestTemplate restTemplate = new RestTemplate();
        String url = "https://booking-com15.p.rapidapi.com/api/v1/hotels/searchDestination";

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("query", query);

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-rapidapi-key", apiKey);
        headers.set("x-rapidapi-host", apiHost);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    builder.toUriString(),
                    HttpMethod.GET,
                    entity,
                    String.class);
            return response.getBody();
        } catch (org.springframework.web.client.HttpStatusCodeException e) {
            e.printStackTrace();
            if (e.getStatusCode().value() == 429) {
                System.out.println("API Quota exceeded (429) during Destination Search. Returning mock data.");
                return getMockDestination(query);
            }
            return createErrorJson(
                    "Destination Search API Error (" + e.getStatusCode() + "): " + e.getResponseBodyAsString());
        } catch (Exception e) {
            e.printStackTrace();
            return createErrorJson("Destination Search Error: " + e.getMessage());
        }
    }

    private String getMockDestination(String query) {
        // 簡易Mock Destinationデータ返却
        String destId = "-246227"; // デフォルト値 (Tokyo)
        String lowerQuery = query.toLowerCase();
        if (lowerQuery.contains("osaka"))
            destId = "-240905";
        else if (lowerQuery.contains("fukuoka"))
            destId = "-238323";
        else if (lowerQuery.contains("sapporo"))
            destId = "-245745";
        else if (lowerQuery.contains("bellustar"))
            destId = "hotel_bellustar_tokyo";
        else if (lowerQuery.contains("intercontinental"))
            return "{\"data\": [{\"dest_id\": \"hotel_ana_intercontinental_tokyo\", \"search_type\": \"PROPERTY\", \"name\": \""
                    + query + "\"}]}";

        return "{\"data\": [{\"dest_id\": \"" + destId + "\", \"search_type\": \"CITY\", \"name\": \"" + query
                + "\"}]}";
    }

    /**
     * 宿泊施設検索 API 呼び出し (Booking.com)
     */
    public String searchHotels(String destId, String searchType, String checkinDate, String checkoutDate,
            String adults) {
        // テスト時Mockデータ返却
        if (apiKey == null || apiKey.isEmpty() || apiKey.equals("YOUR_API_KEY")) {
            return getMockHotels(destId);
        }

        RestTemplate restTemplate = new RestTemplate();
        String url = "https://booking-com15.p.rapidapi.com/api/v1/hotels/searchHotels";

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("dest_id", destId)
                .queryParam("search_type", searchType)
                .queryParam("arrival_date", checkinDate)
                .queryParam("departure_date", checkoutDate)
                .queryParam("adults", adults)
                .queryParam("room_qty", "1")
                .queryParam("page_number", "1")
                .queryParam("sort_by", "popularity")
                .queryParam("currency_code", "JPY");

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-rapidapi-key", apiKey);
        headers.set("x-rapidapi-host", apiHost);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    builder.toUriString(),
                    HttpMethod.GET,
                    entity,
                    String.class);
            return response.getBody();
        } catch (org.springframework.web.client.HttpStatusCodeException e) {
            e.printStackTrace();
            if (e.getStatusCode().value() == 429) {
                System.out.println("API Quota exceeded (429). Returning mock data.");
                return getMockHotels(destId);
            }
            return createErrorJson("API Error (" + e.getStatusCode() + "): " + e.getResponseBodyAsString());
        } catch (Exception e) {
            e.printStackTrace();
            return createErrorJson("Search Error: " + e.getMessage());
        }
    }

    private String createErrorJson(String message) {
        try {
            com.fasterxml.jackson.databind.node.ObjectNode errorNode = objectMapper.createObjectNode();
            errorNode.put("error", message);
            return objectMapper.writeValueAsString(errorNode);
        } catch (Exception e) {
            return "{\"error\": \"Fatal Error: " + e.getMessage().replace("\"", "'") + "\"}";
        }
    }

    private String getMockHotels(String destId) {
        // 簡易Mockデータ生成
        String bellustarMock = "{\"property\": {\"name\": \"BELLUSTAR TOKYO Pan Pacific Hotel\", \"reviewScore\": 9.5, \"reviewCount\": 120, \"priceBreakdown\": {\"grossAmount\": {\"value\": 125000}}, \"photoUrls\": [\"https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=500\"], \"wishlistName\": \"Shinjuku, Tokyo\", \"address\": \"1-29-1 Kabukicho, Shinjuku-ku, Tokyo, Japan\"}, \"languages\": [\"日本語\", \"English\"]}";
        String icMock = "{\"property\": {\"name\": \"ANA InterContinental Tokyo\", \"reviewScore\": 8.8, \"reviewCount\": 2100, \"priceBreakdown\": {\"grossAmount\": {\"value\": 45000}}, \"photoUrls\": [\"https://images.unsplash.com/photo-1566073771259-6a8506099945?w=500\"], \"wishlistName\": \"Minato, Tokyo\", \"address\": \"1-12-33 Akasaka, Minato-ku, Tokyo, Japan\"}, \"languages\": [\"日本語\", \"English\"]}";

        return "{\"data\": {\"hotels\": [" +
                "{\"property\": {\"name\": \"[Mock] Hotel Sunroute Plaza Shinjuku\", \"reviewScore\": 8.5, \"reviewCount\": 12450, \"priceBreakdown\": {\"grossAmount\": {\"value\": 18500}}, \"photoUrls\": [\"https://images.unsplash.com/photo-1566073771259-6a8506099945?w=500\"], \"wishlistName\": \"Shinjuku, Tokyo\", \"address\": \"2-2-1 Yoyogi, Shibuya-ku, Tokyo, Japan\"}, \"languages\": [\"日本語\", \"English\"]},"
                +
                "{\"property\": {\"name\": \"[Mock] Park Hyatt Tokyo\", \"reviewScore\": 9.2, \"reviewCount\": 3500, \"priceBreakdown\": {\"grossAmount\": {\"value\": 85000}}, \"photoUrls\": [\"https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?w=500\"], \"wishlistName\": \"Shinjuku, Tokyo\", \"address\": \"3-7-1-2 Nishi Shinjuku, Shinjuku-ku, Tokyo, Japan\"}, \"languages\": [\"日本語\", \"English\", \"French\"]},"
                +
                bellustarMock + "," + icMock +
                "]}}";
    }

    // 既存呼出方式維持用オーバーロード
    public String searchHotels(String destId, String checkinDate, String checkoutDate, String adults) {
        return searchHotels(destId, "CITY", checkinDate, checkoutDate, adults);
    }
}

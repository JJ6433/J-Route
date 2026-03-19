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
public class FlightService {

    // propertiesから値読込
    @Value("${rapidapi.key}")
    private String apiKey;

    @Value("${rapidapi.host}")
    private String apiHost;

    /**
     * 航空券検索 API 呼び出し
     * @param fromId 出発地 IATA コード (例: SEL.AA1)
     * @param toId 到着地 IATA コード (例: TYO.AA1)
     * @param departDate 出発日 (YYYY-MM-DD)
     * @return API 応答 JSON 文字列
     */
    public String searchFlights(String fromId, String toId, String departDate, String returnDate) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://booking-com15.p.rapidapi.com/api/v1/flights/searchFlights";
        
        // 往復判定 (returnDate有無)
        String itineraryType = (returnDate != null && !returnDate.isEmpty()) ? "ROUND_TRIP" : "ONE_WAY";

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("fromId", fromId)
                .queryParam("toId", toId)
                .queryParam("departDate", departDate)
                .queryParam("itinerary_type", itineraryType) // 動的設定
                .queryParam("pageNo", "1")
                .queryParam("adults", "1")
                .queryParam("currency_code", "JPY");

        // 往復時returnDate追加
        if ("ROUND_TRIP".equals(itineraryType)) {
            builder.queryParam("returnDate", returnDate);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-rapidapi-key", apiKey);
        headers.set("x-rapidapi-host", apiHost);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    builder.toUriString(), 
                    HttpMethod.GET, 
                    entity, 
                    String.class
            );
            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\": \"API呼び出し中にエラーが発生\"}";
        }
    }
    
    public String searchLocation(String query) {
        RestTemplate restTemplate = new RestTemplate();
        
        String url = "https://booking-com15.p.rapidapi.com/api/v1/flights/searchDestination"; 

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("query", query); // パラメータ自動付加

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-rapidapi-key", apiKey);
        headers.set("x-rapidapi-host", apiHost);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    builder.toUriString(), HttpMethod.GET, entity, String.class);
            return response.getBody();
        } catch (Exception e) {
            return "{\"error\": \"位置検索中にエラーが発生: " + e.getMessage() + "\"}";
        }
    }
}
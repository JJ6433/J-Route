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

    // application.properties에서 값을 읽어옵니다.
    @Value("${rapidapi.key}")
    private String apiKey;

    @Value("${rapidapi.host}")
    private String apiHost;

    /**
     * 항공권 검색 API 호출
     * @param fromId 출발지 IATA 코드 (예: SEL.AA1)
     * @param toId 도착지 IATA 코드 (예: TYO.AA1)
     * @param departDate 출발일 (YYYY-MM-DD)
     * @return API 응답 JSON 문자열
     */
    public String searchFlights(String fromId, String toId, String departDate, String returnDate) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://booking-com15.p.rapidapi.com/api/v1/flights/searchFlights";
        
        // 왕복 여부 판단 (returnDate가 있으면 ROUND_TRIP, 없으면 ONE_WAY)
        String itineraryType = (returnDate != null && !returnDate.isEmpty()) ? "ROUND_TRIP" : "ONE_WAY";

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("fromId", fromId)
                .queryParam("toId", toId)
                .queryParam("departDate", departDate)
                .queryParam("itinerary_type", itineraryType) // 동적으로 설정
                .queryParam("pageNo", "1")
                .queryParam("adults", "1")
                .queryParam("currency_code", "JPY");

        // 왕복일 경우 returnDate 파라미터 추가
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
            return "{\"error\": \"API 호출 중 오류 발생\"}";
        }
    }
    
    public String searchLocation(String query) {
        RestTemplate restTemplate = new RestTemplate();
        
        String url = "https://booking-com15.p.rapidapi.com/api/v1/flights/searchDestination"; 

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("query", query); // 여기서 파라미터를 자동으로 붙여줍니다.

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-rapidapi-key", apiKey);
        headers.set("x-rapidapi-host", apiHost);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    builder.toUriString(), HttpMethod.GET, entity, String.class);
            return response.getBody();
        } catch (Exception e) {
            return "{\"error\": \"위치 검색 중 오류 발생: " + e.getMessage() + "\"}";
        }
    }
}
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
     * 숙소 검색 API 호출 (Booking.com)
     */
    public String searchHotels(String destId, String checkinDate, String checkoutDate, String adults) {
        RestTemplate restTemplate = new RestTemplate();
        // Booking.com 숙소 검색 엔드포인트
        String url = "https://booking-com15.p.rapidapi.com/api/v1/hotels/searchHotels";

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("dest_id", destId)
                .queryParam("search_type", "CITY") // 도시 기준 검색
                .queryParam("arrival_date", checkinDate)
                .queryParam("departure_date", checkoutDate)
                .queryParam("adults", adults)
                .queryParam("room_qty", "1") // 방 1개 기준
                .queryParam("page_number", "1")
                .queryParam("currency_code", "JPY"); // 엔화 표시

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
            // 에러 메시지 안의 쌍따옴표(")나 줄바꿈 때문에 JSON 파싱이 깨지는 것을 방지합니다.
            String safeErrorMessage = e.getMessage() != null ? e.getMessage().replace("\"", "'").replace("\n", " ") : "알 수 없는 오류";
            return "{\"error\": \"" + safeErrorMessage + "\"}";
        }
    }
}
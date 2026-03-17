package com.blog.service;

import com.blog.dto.ExchangeDto;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ExchangeService {

    // 💡 키 발급이 필요 없는 완전 무료 개방형 환율 API! (기준: JPY)
    private static final String EXCHANGE_API_URL = "https://open.er-api.com/v6/latest/JPY";

    public ExchangeDto getLatestExchangeRate() {
        RestTemplate restTemplate = new RestTemplate();
        ExchangeDto dto = new ExchangeDto();

        try {
            // API에서 JSON 데이터 가져오기
            JsonNode root = restTemplate.getForObject(EXCHANGE_API_URL, JsonNode.class);

            if (root != null && "success".equals(root.path("result").asText())) {
                // 원화(KRW) 환율 정보 추출
                double krwRate = root.path("rates").path("KRW").asDouble();
                String updateTime = root.path("time_last_update_utc").asText();

                dto.setJpyToKrw(krwRate);
                dto.setJpy100ToKrw(Math.round(krwRate * 100 * 100.0) / 100.0); // 소수점 둘째 자리까지 깔끔하게!
                
                // 시간 포맷 예쁘게 다듬기 (예: "Thu, 14 Mar 2026 00:00:00 +0000" -> 앞부분만 사용)
                dto.setLastUpdateTime(updateTime.substring(0, 16)); 
            }
        } catch (Exception e) {
            System.err.println("❌ 환율 API 호출 에러: " + e.getMessage());
            // 에러 발생 시 기본값 세팅 (앱이 뻗지 않도록 방어!)
            dto.setJpyToKrw(9.0);
            dto.setJpy100ToKrw(900.0);
            dto.setLastUpdateTime("情報取得失敗 (基本値表示)");
        }

        return dto;
    }
}
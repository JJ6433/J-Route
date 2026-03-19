package com.blog.service;

import com.blog.dto.ExchangeDto;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ExchangeService {

    // 💡 無料為替API (基準: JPY)
    private static final String EXCHANGE_API_URL = "https://open.er-api.com/v6/latest/JPY";

    public ExchangeDto getLatestExchangeRate() {
        RestTemplate restTemplate = new RestTemplate();
        ExchangeDto dto = new ExchangeDto();

        try {
            // APIからJSONデータ取得
            JsonNode root = restTemplate.getForObject(EXCHANGE_API_URL, JsonNode.class);

            if (root != null && "success".equals(root.path("result").asText())) {
                // KRW為替レート情報抽出
                double krwRate = root.path("rates").path("KRW").asDouble();
                String updateTime = root.path("time_last_update_utc").asText();

                dto.setJpyToKrw(krwRate);
                dto.setJpy100ToKrw(Math.round(krwRate * 100 * 100.0) / 100.0); // 小数点第2位まで丸め
                
                // 時間フォーマット整形
                dto.setLastUpdateTime(updateTime.substring(0, 16)); 
            }
        } catch (Exception e) {
            System.err.println("❌ 為替 API 呼び出しエラー: " + e.getMessage());
            // エラー時デフォルト値セッティング
            dto.setJpyToKrw(9.0);
            dto.setJpy100ToKrw(900.0);
            dto.setLastUpdateTime("情報取得失敗 (基本値表示)");
        }

        return dto;
    }
}
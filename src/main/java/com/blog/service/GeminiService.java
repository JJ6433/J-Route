package com.blog.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    @Value("${google.gemini.api-key}")
    private String apiKey;

    @Value("${google.gemini.url}")
    private String apiUrl;

    private final RestClient restClient;
    private final ApiLogService apiLogService;

    public GeminiService(ApiLogService apiLogService) {
        this.restClient = RestClient.create();
        this.apiLogService = apiLogService;
    }

    public String getPlan(String region, int dayCount, String style, String companion) {
        String prompt = buildPrompt(region, dayCount, style, companion);
        long startTime = System.currentTimeMillis();

        // Gemini Request Body
        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", prompt)))));

        try {
            System.out.println("Calling Gemini API: " + apiUrl);
            Map<String, Object> response = restClient.post()
                    .uri(apiUrl + "?key=" + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(Map.class);

            long endTime = System.currentTimeMillis();
            apiLogService.saveLog("Gemini", "/v1beta/models/gemini-pro:generateContent", "SUCCESS",
                    (endTime - startTime), null);

            System.out.println("Gemini API Response received.");
            return extractContentFromResponse(response);
        } catch (org.springframework.web.client.HttpStatusCodeException e) {
            long endTime = System.currentTimeMillis();
            String errorBody = e.getResponseBodyAsString();
            apiLogService.saveLog("Gemini", "/v1beta/models/gemini-pro:generateContent", "FAIL", (endTime - startTime),
                    errorBody);

            System.err.println("Gemini API Error [" + e.getStatusCode() + "]: " + errorBody);
            if (e.getStatusCode().value() == 400) {
                return "{\"error\": \"APIリクエストが不正です。パラメータを確認してください。(400)\"}";
            }
            if (e.getStatusCode().value() == 503) {
                return "{\"error\": \"AIモデルの負荷が高いため、現在ご利用いただけません。10〜20秒後に再度お試しください。(503)\"}";
            }
            return "{\"error\": \"AI request failed (" + e.getStatusCode() + "): " + e.getStatusText() + "\"}";
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            apiLogService.saveLog("Gemini", "/v1beta/models/gemini-pro:generateContent", "FAIL", (endTime - startTime),
                    e.getMessage());

            System.err.println("Gemini General Error: " + e.getMessage());
            e.printStackTrace();
            return "{\"error\": \"AI request failed: " + e.getMessage() + "\"}";
        }
    }

    private String buildPrompt(String region, int days, String style, String companion) {
        String companionInfo = (companion != null && !companion.isEmpty()) ? "同行者は \"" + companion + "\" です。" : "";
        return "あなたはプロの旅行プランナーです。" +
                region + "を対象に " + days + "日間の旅行計画を立ててください。" +
                "旅行のスタイルは \"" + style + "\" です。" +
                companionInfo +
                "**重要: 各日程には必ず地元の美味しいお店での昼食(Lunch)と夕食(Dinner)を含めてください。**" +
                "同行者のタイプに合わせて、最適な観光スポットや飲食店を提案してください。" +
                "場所の名前とメモは必ず日本語で作成してください。(ただし、keywordは画像検索のために必ず英語の単語で作成してください。)" +
                "応答は必ず以下のJSON構造のみを含める必要があり、いかなる説明やマークダウン形式(```jsonなど)も含めないでください: " +
                "{ \"days\": [ { \"day\": 1, \"items\": [ { \"time\": \"09:00\", \"title\": \"場所名\", \"memo\": \"簡単な説明\", \"keyword\": \"English noun for image search (e.g. shrine, skyscraper, food)\" } ] } ] }";
    }

    @SuppressWarnings("unchecked")
    private String extractContentFromResponse(Map<String, Object> response) {
        try {
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
            if (candidates != null && !candidates.isEmpty()) {
                Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
                List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
                if (parts != null && !parts.isEmpty()) {
                    String text = (String) parts.get(0).get("text");

                    // Remove markdown code blocks if present
                    text = text.replaceAll("```json", "").replaceAll("```", "").trim();

                    int start = text.indexOf("{");
                    int end = text.lastIndexOf("}");
                    if (start != -1 && end != -1 && end > start) {
                        return text.substring(start, end + 1).trim();
                    }
                    return text.trim();
                }
            }
        } catch (Exception e) {
            System.err.println("Gemini Parsing Error: " + e.getMessage());
            return "{\"error\": \"Failed to parse AI response\"}";
        }
        return "{\"error\": \"No content generated\"}";
    }
}

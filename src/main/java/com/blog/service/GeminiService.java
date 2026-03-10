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

    public String getPlan(String region, String startDate, String endDate, int adults, int children, String style,
            String companion, String accommodationName, String accommodationAddress) {
        String prompt = buildPrompt(region, startDate, endDate, adults, children, style, companion, accommodationName,
                accommodationAddress);
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
            return "{\"error\": \"AI request failed: \" + e.getMessage() + \"\"}";
        }
    }

    private String buildPrompt(String region, String startDate, String endDate, int adults, int children, String style,
            String companion, String accommodationName, String accommodationAddress) {
        String travelerInfo = String.format("旅行客は大人%d名, %s%d名です。", adults, (children > 0 ? "子供 " : ""), children);
        String companionInfo = (companion != null && !companion.isEmpty()) ? "同行者は \"" + companion + "\" です。" : "";
        String accInfo = String.format("宿泊先は 「%s (%s)」です。毎日の行程は必ずこの宿泊先から出発し、最後はこの宿泊先に戻る動線で構成してください。",
                accommodationName, accommodationAddress);

        return "あなたはプロの旅行プランナーです。 " +
                region + "を対象に " + startDate + " から " + endDate + " までの旅行計画を立ててください。 " +
                travelerInfo + " " + companionInfo + " " +
                "旅行のスタイルは \"" + style + "\" です。 " +
                accInfo +
                "**重要: 各日程には必ず現地のおいしい店でのランチ(Lunch)とディナー(Dinner)を含めてください。**" +
                "**動線最適化: 宿泊先を拠点とし、前の場所から次の場所への移動手段(徒歩、バス番号、地下鉄路線など)と予想所要時間、費用を考慮して配置してください。**" +
                "**場所情報: 各場所の予想営業時間(例: 09:00~18:00)とカード決済の可否(Yes/No)を把握して含めてください。**" +
                "**費用計算: 各項目の予想費用(単位: 円)を 'expense' : 1500 のような整数形式で含めてください。**" +
                "**天気情報: 設定された日付の該当地域での '予想天気(晴れ、曇り、雨など)' と '予想気温(最高/最低)' を含めてください。すべて日本語で記述してください。**" +
                "**画像検索の最適化: 'title'は必ずGoogleマップで検索可能な、具体的で実在するランドマークや店舗名にしてください。**" +
                "**言語設定: 場所名、メモ(説明)、天気の詳細(desc)などは必ず『日本語』で作成してください。韓国語は絶対に使用しないでください。韓国語が出力された場合はエラーとみなします。** (ただし、keywordは画像検索のために必ず英単語で作成してください。)"
                +
                "応答は必ず以下のJSON構造のみを含める必要があり、いかなる説明やマークダウン形式(```jsonなど)も含めないでください: " +
                "{ \"trip_info\": { \"start_date\": \"YYYY-MM-DD\", \"end_date\": \"YYYY-MM-DD\", \"adults\": 2, \"children\": 0, \"accommodation\": \"ホテル名\" }, "
                +
                "\"weather\": [ { \"date\": \"YYYY-MM-DD\", \"desc\": \"晴れ\", \"temp\": \"20°C / 10°C\" } ], " +
                "\"days\": [ { \"day\": 1, \"date\": \"YYYY-MM-DD\", \"items\": [ { \"time\": \"09:00\", \"title\": \"具体的な場所の名前\", \"memo\": \"場所の説明\", \"transport\": \"移動手段(例: バス10分)\", \"hours\": \"営業時間(例: 09:00~18:00)\", \"card\": \"Yes/No\", \"keyword\": \"English noun for image search\", \"expense\": 1500 } ] } ] }";
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

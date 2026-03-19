package com.blog.service;

import com.blog.dto.WeatherDto;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class WeatherService {

    // Open-Meteo APIキー不要

    public String getCityKey(String region) {
        if (region == null)
            return "Tokyo";
        if (region.contains("東京") || region.equalsIgnoreCase("Tokyo"))
            return "Tokyo";
        if (region.contains("大阪") || region.equalsIgnoreCase("Osaka"))
            return "Osaka";
        if (region.contains("名古屋") || region.equalsIgnoreCase("Nagoya"))
            return "Nagoya";
        if (region.contains("福岡") || region.equalsIgnoreCase("Fukuoka"))
            return "Fukuoka";
        if (region.contains("札幌") || region.equalsIgnoreCase("Sapporo"))
            return "Sapporo";
        if (region.contains("静岡") || region.equalsIgnoreCase("Shizuoka"))
            return "Shizuoka";
        if (region.contains("広島") || region.equalsIgnoreCase("Hiroshima"))
            return "Hiroshima";
        if (region.contains("沖縄") || region.equalsIgnoreCase("Okinawa"))
            return "Okinawa";
        return "Tokyo";
    }

    // 緯度・経度設定
    private double[] getCoordinates(String city) {
        switch (city) {
            case "Tokyo":
                return new double[] { 35.6895, 139.6917 };
            case "Osaka":
                return new double[] { 34.6937, 135.5023 };
            case "Nagoya":
                return new double[] { 35.1815, 136.9066 };
            case "Fukuoka":
                return new double[] { 33.5902, 130.4017 };
            case "Sapporo":
                return new double[] { 43.0618, 141.3545 };
            case "Shizuoka":
                return new double[] { 34.9756, 138.3828 };
            case "Hiroshima":
                return new double[] { 34.3853, 132.4553 };
            case "Okinawa":
                return new double[] { 26.2124, 127.6809 };
            default:
                return new double[] { 35.6895, 139.6917 };
        }
    }

    // 全国天気照会（16日間）
    public WeatherDto getWeather(String city, String cityNameJa, String targetDate) {
        if (targetDate == null || targetDate.isEmpty())
            targetDate = LocalDate.now().toString();

        double[] coords = getCoordinates(city);
        // 16日間予報データリクエスト
        String url = String.format(
                "https://api.open-meteo.com/v1/forecast?latitude=%f&longitude=%f&hourly=temperature_2m,weather_code&timezone=Asia/Tokyo&forecast_days=16",
                coords[0], coords[1]);

        RestTemplate restTemplate = new RestTemplate();
        WeatherDto dto = new WeatherDto();
        dto.setCity(city);
        dto.setCityNameJa(cityNameJa);

        try {
            JsonNode root = restTemplate.getForObject(url, JsonNode.class);
            if (root != null && root.has("hourly")) {
                JsonNode hourly = root.path("hourly");
                JsonNode times = hourly.path("time");
                JsonNode temps = hourly.path("temperature_2m");
                JsonNode codes = hourly.path("weather_code");

                // 現在時刻検索
                int currentHour = LocalTime.now().getHour();
                String targetPrefix = targetDate + "T" + String.format("%02d:00", currentHour);

                int targetIndex = -1;
                for (int i = 0; i < times.size(); i++) {
                    if (times.get(i).asText().startsWith(targetPrefix)) {
                        targetIndex = i;
                        break;
                    }
                }

                // 見つからない場合は12時として処理
                if (targetIndex == -1) {
                    String fallbackPrefix = targetDate + "T12:00";
                    for (int i = 0; i < times.size(); i++) {
                        if (times.get(i).asText().startsWith(fallbackPrefix)) {
                            targetIndex = i;
                            break;
                        }
                    }
                }

                if (targetIndex != -1) {
                    dto.setTemperature(temps.get(targetIndex).asDouble());
                    int weatherCode = codes.get(targetIndex).asInt();
                    boolean isNight = currentHour < 6 || currentHour > 18; // 昼夜区分
                    String[] weatherInfo = interpretWmoCode(weatherCode, isNight);

                    dto.setDescription(weatherInfo[0]);
                    dto.setIconEmoji(weatherInfo[1]);
                    dto.setOutfitHint(recommendOutfit(dto.getTemperature()));
                }
            }
        } catch (Exception e) {
            System.err.println("❌ [" + city + "] Open-Meteo API エラー: " + e.getMessage());
        }
        return dto;
    }

    // 時間別天気照会
    public List<WeatherDto> getHourlyWeather(String city, String cityNameJa, String targetDate) {
        double[] coords = getCoordinates(city);
        String url = String.format(
                "https://api.open-meteo.com/v1/forecast?latitude=%f&longitude=%f&hourly=temperature_2m,weather_code&timezone=Asia/Tokyo&forecast_days=16",
                coords[0], coords[1]);

        RestTemplate restTemplate = new RestTemplate();
        List<WeatherDto> hourlyList = new ArrayList<>();

        try {
            JsonNode root = restTemplate.getForObject(url, JsonNode.class);
            if (root != null && root.has("hourly")) {
                JsonNode hourly = root.path("hourly");
                JsonNode times = hourly.path("time");
                JsonNode temps = hourly.path("temperature_2m");
                JsonNode codes = hourly.path("weather_code");

                for (int i = 0; i < times.size(); i++) {
                    String dtTxt = times.get(i).asText();

                    if (dtTxt.startsWith(targetDate)) {
                        int hour = Integer.parseInt(dtTxt.substring(11, 13));

                        // 3時間間隔抽出
                        if (hour % 3 == 0) {
                            WeatherDto dto = new WeatherDto();
                            dto.setCity(city);
                            dto.setCityNameJa(cityNameJa);
                            dto.setTemperature(temps.get(i).asDouble());

                            int weatherCode = codes.get(i).asInt();
                            boolean isNight = hour < 6 || hour > 18;
                            String[] weatherInfo = interpretWmoCode(weatherCode, isNight);

                            dto.setDescription(weatherInfo[0]);
                            dto.setIconEmoji(weatherInfo[1]);
                            dto.setTime(String.format("%02d時", hour));
                            dto.setOutfitHint(recommendOutfit(dto.getTemperature()));

                            hourlyList.add(dto);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("❌ [" + city + "] 時間別天気 API エラー: " + e.getMessage());
        }
        return hourlyList;
    }

    // 特定期間天気照会（Gemini用）
    public List<WeatherDto> getWeatherForRange(String city, String cityNameJa, String startDate, String endDate) {
        double[] coords = getCoordinates(city);
        // forecast_days=16 matches the API limit
        String url = String.format(
                "https://api.open-meteo.com/v1/forecast?latitude=%f&longitude=%f&daily=weather_code,temperature_2m_max,temperature_2m_min&timezone=Asia/Tokyo&forecast_days=16",
                coords[0], coords[1]);

        RestTemplate restTemplate = new RestTemplate();
        List<WeatherDto> dailyList = new ArrayList<>();

        try {
            JsonNode root = restTemplate.getForObject(url, JsonNode.class);
            if (root != null && root.has("daily")) {
                JsonNode daily = root.path("daily");
                JsonNode times = daily.path("time");
                JsonNode maxTemps = daily.path("temperature_2m_max");
                JsonNode minTemps = daily.path("temperature_2m_min");
                JsonNode codes = daily.path("weather_code");

                LocalDate start = LocalDate.parse(startDate);
                LocalDate end = LocalDate.parse(endDate);

                for (int i = 0; i < times.size(); i++) {
                    LocalDate date = LocalDate.parse(times.get(i).asText());
                    if ((date.isEqual(start) || date.isAfter(start)) && (date.isEqual(end) || date.isBefore(end))) {
                        WeatherDto dto = new WeatherDto();
                        dto.setCity(city);
                        dto.setCityNameJa(cityNameJa);
                        dto.setDate(date.toString());
                        dto.setTemperatureMax(maxTemps.get(i).asDouble());
                        dto.setTemperatureMin(minTemps.get(i).asDouble());

                        // For display/prompt purposes
                        dto.setTemperature(maxTemps.get(i).asDouble());

                        int weatherCode = codes.get(i).asInt();
                        String[] weatherInfo = interpretWmoCode(weatherCode, false);
                        dto.setDescription(weatherInfo[0]);
                        dto.setIconEmoji(weatherInfo[1]);

                        dailyList.add(dto);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("❌ [" + city + "] 期間天気 API エラー: " + e.getMessage());
        }
        return dailyList;
    }

    // WMO天気コード変換
    private String[] interpretWmoCode(int code, boolean isNight) {
        if (code == 0)
            return new String[] { "快晴", isNight ? "🌙" : "☀️" };
        if (code == 1 || code == 2)
            return new String[] { "晴れ時々曇り", isNight ? "☁️" : "🌤️" };
        if (code == 3)
            return new String[] { "曇り", "☁️" };
        if (code == 45 || code == 48)
            return new String[] { "霧", "🌫️" };
        if (code >= 51 && code <= 57)
            return new String[] { "霧雨", "🌧️" };
        if (code >= 61 && code <= 67)
            return new String[] { "雨", "☔" };
        if (code >= 71 && code <= 77)
            return new String[] { "雪", "❄️" };
        if (code >= 80 && code <= 82)
            return new String[] { "にわか雨", "🌦️" };
        if (code >= 85 && code <= 86)
            return new String[] { "雪", "🌨️" };
        if (code >= 95 && code <= 99)
            return new String[] { "雷雨", "⛈️" };
        return new String[] { "不明", "🌈" };
    }

    private String recommendOutfit(double temp) {
        if (temp >= 28)
            return "とても暑いです！半袖やノースリーブが必須です😎";
        if (temp >= 23)
            return "半袖や薄手の綿Tシャツがちょうどいい気候です👕";
        if (temp >= 20)
            return "薄手のカーディガンや長袖シャツを用意しましょう🧥";
        if (temp >= 15)
            return "ジャケットやスウェットが着やすい季節です🍂";
        if (temp >= 10)
            return "トレンチコートや厚手のニットが必要です🧣";
        if (temp >= 5)
            return "コートやヒートテックなど、しっかり防寒を❄️";
        return "ダウンジャケット、マフラーで完全防寒してください⛄";
    }
}
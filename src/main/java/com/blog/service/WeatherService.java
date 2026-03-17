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

    // 💡 Open-Meteo는 API 키가 필요 없습니다!
    
    // 💡 도시별 위도(Latitude)와 경도(Longitude) 세팅
    private double[] getCoordinates(String city) {
        switch(city) {
            case "Tokyo": return new double[]{35.6895, 139.6917};
            case "Osaka": return new double[]{34.6937, 135.5023};
            case "Nagoya": return new double[]{35.1815, 136.9066};
            case "Fukuoka": return new double[]{33.5902, 130.4017};
            case "Sapporo": return new double[]{43.0618, 141.3545};
            case "Shizuoka": return new double[]{34.9756, 138.3828};
            case "Hiroshima": return new double[]{34.3853, 132.4553};
            case "Okinawa": return new double[]{26.2124, 127.6809};
            default: return new double[]{35.6895, 139.6917};
        }
    }

    // 1️⃣ 전국 날씨 (현재 시간 기준 16일 치 검색 가능)
    public WeatherDto getWeather(String city, String cityNameJa, String targetDate) {
        if (targetDate == null || targetDate.isEmpty()) targetDate = LocalDate.now().toString();
        
        double[] coords = getCoordinates(city);
        // 💡 16일치 예보 데이터를 요청합니다! (forecast_days=16)
        String url = String.format("https://api.open-meteo.com/v1/forecast?latitude=%f&longitude=%f&hourly=temperature_2m,weather_code&timezone=Asia/Tokyo&forecast_days=16", coords[0], coords[1]);
        
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

                // 현재 시간 찾기 (예: "15:00")
                int currentHour = LocalTime.now().getHour();
                String targetPrefix = targetDate + "T" + String.format("%02d:00", currentHour);

                int targetIndex = -1;
                for (int i = 0; i < times.size(); i++) {
                    if (times.get(i).asText().startsWith(targetPrefix)) {
                        targetIndex = i; break;
                    }
                }
                
                // 만약 정확한 시간이 없으면 해당 날짜의 12시로 임시 처리
                if (targetIndex == -1) {
                    String fallbackPrefix = targetDate + "T12:00";
                    for (int i = 0; i < times.size(); i++) {
                        if (times.get(i).asText().startsWith(fallbackPrefix)) {
                            targetIndex = i; break;
                        }
                    }
                }

                if (targetIndex != -1) {
                    dto.setTemperature(temps.get(targetIndex).asDouble());
                    int weatherCode = codes.get(targetIndex).asInt();
                    boolean isNight = currentHour < 6 || currentHour > 18; // 밤낮 구분
                    String[] weatherInfo = interpretWmoCode(weatherCode, isNight);
                    
                    dto.setDescription(weatherInfo[0]);
                    dto.setIconEmoji(weatherInfo[1]);
                    dto.setOutfitHint(recommendOutfit(dto.getTemperature()));
                }
            }
        } catch (Exception e) {
            System.err.println("❌ [" + city + "] Open-Meteo API 에러: " + e.getMessage());
        }
        return dto;
    }

    // 2️⃣ 상세 시간별 날씨
    public List<WeatherDto> getHourlyWeather(String city, String cityNameJa, String targetDate) {
        double[] coords = getCoordinates(city);
        String url = String.format("https://api.open-meteo.com/v1/forecast?latitude=%f&longitude=%f&hourly=temperature_2m,weather_code&timezone=Asia/Tokyo&forecast_days=16", coords[0], coords[1]);
        
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
                        
                        // 💡 차트가 너무 붐비지 않도록 3시간 간격으로 추출!
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
            System.err.println("❌ [" + city + "] 시간별 날씨 API 에러: " + e.getMessage());
        }
        return hourlyList;
    }

    // 💡 WMO(세계기상기구) 날씨 코드를 일본어 설명과 예쁜 이모지로 변환!
    private String[] interpretWmoCode(int code, boolean isNight) {
        if (code == 0) return new String[]{"快晴", isNight ? "🌙" : "☀️"};
        if (code == 1 || code == 2) return new String[]{"晴れ時々曇り", isNight ? "☁️" : "🌤️"};
        if (code == 3) return new String[]{"曇り", "☁️"};
        if (code == 45 || code == 48) return new String[]{"霧", "🌫️"};
        if (code >= 51 && code <= 57) return new String[]{"霧雨", "🌧️"};
        if (code >= 61 && code <= 67) return new String[]{"雨", "☔"};
        if (code >= 71 && code <= 77) return new String[]{"雪", "❄️"};
        if (code >= 80 && code <= 82) return new String[]{"にわか雨", "🌦️"};
        if (code >= 85 && code <= 86) return new String[]{"雪", "🌨️"};
        if (code >= 95 && code <= 99) return new String[]{"雷雨", "⛈️"};
        return new String[]{"不明", "🌈"};
    }

    private String recommendOutfit(double temp) {
        if (temp >= 28) return "とても暑いです！半袖やノースリーブが必須です😎";
        if (temp >= 23) return "半袖や薄手の綿Tシャツがちょうどいい気候です👕";
        if (temp >= 20) return "薄手のカーディガンや長袖シャツを用意しましょう🧥";
        if (temp >= 15) return "ジャケットやスウェットが着やすい季節です🍂";
        if (temp >= 10) return "トレンチコートや厚手のニットが必要です🧣";
        if (temp >= 5)  return "コートやヒートテックなど、しっかり防寒を❄️";
        return "ダウンジャケット、マフラーで完全防寒してください⛄";
    }
}
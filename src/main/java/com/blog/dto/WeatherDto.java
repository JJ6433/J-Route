package com.blog.dto;

import lombok.Data;

@Data
public class WeatherDto {
    private String city; // 영문 도시 이름 (예: Tokyo)
    private String cityNameJa; // 일본어 도시 이름 (예: 東京)
    private double temperature; // 현재 온도
    private String description; // 날씨 상태 (맑음, 비 등)
    private String iconUrl; // 날씨 아이콘 이미지 주소
    private String outfitHint; // 💡 온도에 따른 옷차림 추천 텍스트
    private String time; // 💡 날씨 데이터의 시간 정보 (예: "2024-06-01 12:00:00")
    private String date; // 💡 날짜 정보 (예: "2024-06-01")
    private double temperatureMax; // 최고 온도
    private double temperatureMin; // 최저 온도
    private String iconEmoji; // 💡 날씨 상태에 따른 이모지 (예: "☀️", "🌧️" 등)
}
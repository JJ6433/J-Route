package com.blog.controller;

import com.blog.dto.WeatherDto;
import com.blog.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/weather")
public class WeatherController {

    @Autowired
    private WeatherService weatherService;

    @GetMapping("/weather-result")
    public String showWeatherPage(
            @RequestParam(value = "date", required = false) String date, 
            @RequestParam(value = "cityId", required = false, defaultValue = "ALL") String cityId, 
            Model model) {
        
        model.addAttribute("activeMenu", "weather");

        // 날짜 & 지역 기본값 설정
        String selectedDate = (date != null && !date.isEmpty()) ? date : LocalDate.now().toString();
        model.addAttribute("selectedDate", selectedDate);
        model.addAttribute("selectedCity", cityId);

        Map<String, String> cities = new LinkedHashMap<>();
        cities.put("Tokyo", "東京");
        cities.put("Osaka", "大阪");
        cities.put("Nagoya", "名古屋");
        cities.put("Fukuoka", "福岡");
        cities.put("Sapporo", "札幌");
        cities.put("Shizuoka", "静岡");
        cities.put("Hiroshima", "広島");
        cities.put("Okinawa", "沖縄");

        // 💡 분기 처리: 전국을 골랐을 때 vs 특정 도시를 골랐을 때
        if ("ALL".equals(cityId)) {
            // 전국 모드: 8대 도시의 낮 12시 날씨 (기존 화면)
            List<WeatherDto> weatherList = new ArrayList<>();
            cities.forEach((eng, kor) -> {
                weatherList.add(weatherService.getWeather(eng, kor, selectedDate));
            });
            model.addAttribute("weatherList", weatherList);
            model.addAttribute("viewMode", "ALL");
        } else {
            // 상세 모드: 선택한 지역의 하루치 시간별 날씨 리스트
            String cityNameJa = cities.get(cityId);
            List<WeatherDto> hourlyList = weatherService.getHourlyWeather(cityId, cityNameJa, selectedDate);
            model.addAttribute("hourlyList", hourlyList);
            model.addAttribute("detailCityName", cityNameJa);
            model.addAttribute("viewMode", "DETAIL");
        }

        return "weather/weather-result"; 
    }
}
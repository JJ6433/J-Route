package com.blog.dto;

import lombok.Data;

@Data
public class WeatherDto {
    private String city; // 英文都市名
    private String cityNameJa; // 日本語都市名
    private double temperature; // 現在温度
    private String description; // 天気状態
    private String iconUrl; // 天気アイコンURL
    private String outfitHint; // 💡 温度別服装推奨テキスト
    private String time; // 💡 天気データ時間情報
    private String date; // 💡 日付情報
    private double temperatureMax; // 最高温度
    private double temperatureMin; // 最低温度
    private String iconEmoji; // 💡 天気状態絵文字
}
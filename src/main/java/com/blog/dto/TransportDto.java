package com.blog.dto;

import lombok.Data;

@Data
public class TransportDto {
    private Integer id;             // パス券のID
    private String name;        // パス券の名前
    private String subtitle;    // サブタイトル (例: 全国版)
    private String iconEmoji;   // アイコン絵文字
    private int price;          // 価格
    private String badgeText;   // バッジのテキスト
    private String description; // 説明
}
package com.blog.dto;

import lombok.Data;
import java.time.LocalDateTime;

import lombok.Data;

/**
 * お気に入り DTO (wishlists テーブル)
 * ユーザーが保存した関心の旅行先及び API 商品(宿泊/航空券)
 */
@Data // Getter, Setter自動生成
public class WishlistDto {
    private Long wishlistId;
    private Long userId;
    
    private Long placeId; // 既存独自DB旅行地固有番号
    
    // 新規追加主要フィールド
    private String category; // カテゴリ ('PLACE', 'HOTEL', 'FLIGHT', 'AI')
    private String apiId;    // 外部APIデータ固有ID
    
    private LocalDateTime createdAt;
    private String placeName;
    private String placeRegion;
    private String placeCategory;
    private String imageUrl;
    private Integer price;
}

package com.blog.dto;

import lombok.Data;
import java.sql.Timestamp;

@Data // Getter, Setter等自動生成
public class ReservationDto {
    private int id;
    private String orderId;    
    private Long userId;     
    private String itemName;   
    private String category;   // カテゴリ (HOTEL, FLIGHT)
    private int amount;        
    private String status;     
    private Timestamp createdAt;
    private String imageUrl;
    private String checkIn;
    private String checkOut;
    private String address;
    private String details;

    // 管理者画面用追加フィールド
    private String nickname; // 予約者ニックネーム
}
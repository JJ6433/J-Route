package com.blog.dto;

import lombok.Data;
import java.sql.Timestamp;

@Data // Getter, Setter 등 모든 메서드가 자동 생성됩니다.
public class ReservationDto {
    private int id;
    private String orderId;    
    private Long userId;     
    private String itemName;   
    private String category;   // 카테고리 (HOTEL 또는 FLIGHT)
    private int amount;        
    private String status;     
    private Timestamp createdAt;
}
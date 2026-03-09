package com.blog.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 찜하기 DTO (wishlists 테이블)
 * 사용자가 저장한 관심 여행지 및 API 상품(숙소/항공권)
 */
@Data // Getter, Setter가 자동으로 생성
public class WishlistDto {
    private Long wishlistId;
    private Long userId;
    
    private Long placeId; // 기존 자체 DB 여행지 고유 번호
    
    // 새롭게 추가된 2개의 핵심 필드
    private String category; // 카테고리 ('PLACE', 'HOTEL', 'FLIGHT', 'AI')
    private String apiId;    // 외부 API 데이터의 고유 ID (문자열)
    
    private LocalDateTime createdAt;
    private String placeName;
    private String placeRegion;
    private String placeCategory;
    private String imageUrl;
}
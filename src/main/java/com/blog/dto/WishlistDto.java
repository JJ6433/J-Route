package com.blog.dto;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * 찜하기 DTO (wishlists 테이블)
 * 사용자가 저장한 관심 여행지
 */
@Data
public class WishlistDto {
	private Long wishlistId;
	private Long userId;
	private Long placeId;
	private LocalDateTime createdAt;
	private String placeName;
	private String placeRegion;
	private String placeCategory;
	private String imageUrl;

}

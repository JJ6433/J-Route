package com.blog.dto;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * 여행지 정보 DTO (places 테이블)
 * 관광지, 맛집, 숙소 정보
 */
@Data
public class PlaceDto {
	private Long placeId;
	private String name;
	private String category;
	private String region;
	private String description;
	private String address;
	private String imageUrl;
	private LocalDateTime createdAt;
	private Double avgRating;
	private Integer reviewCount;


}

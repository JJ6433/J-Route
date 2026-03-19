package com.blog.dto;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * 旅行先情報 DTO (places テーブル)
 * 観光地、グルメ、宿泊情報
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

package com.blog.dto;

import java.time.LocalDateTime;

/**
 * 여행지 정보 DTO (places 테이블)
 * 관광지, 맛집, 숙소 정보
 */
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

	public Long getPlaceId() { return placeId; }
	public void setPlaceId(Long placeId) { this.placeId = placeId; }
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public String getCategory() { return category; }
	public void setCategory(String category) { this.category = category; }
	public String getRegion() { return region; }
	public void setRegion(String region) { this.region = region; }
	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }
	public String getAddress() { return address; }
	public void setAddress(String address) { this.address = address; }
	public String getImageUrl() { return imageUrl; }
	public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
	public LocalDateTime getCreatedAt() { return createdAt; }
	public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
	public Double getAvgRating() { return avgRating; }
	public void setAvgRating(Double avgRating) { this.avgRating = avgRating; }
	public Integer getReviewCount() { return reviewCount; }
	public void setReviewCount(Integer reviewCount) { this.reviewCount = reviewCount; }
}

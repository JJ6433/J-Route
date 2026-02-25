package com.blog.dto;

import java.time.LocalDateTime;

/**
 * 찜하기 DTO (wishlists 테이블)
 * 사용자가 저장한 관심 여행지
 */
public class WishlistDto {
	private Long wishlistId;
	private Long userId;
	private Long placeId;
	private LocalDateTime createdAt;
	private String placeName;
	private String placeRegion;
	private String placeCategory;
	private String imageUrl;

	public Long getWishlistId() { return wishlistId; }
	public void setWishlistId(Long wishlistId) { this.wishlistId = wishlistId; }
	public Long getUserId() { return userId; }
	public void setUserId(Long userId) { this.userId = userId; }
	public Long getPlaceId() { return placeId; }
	public void setPlaceId(Long placeId) { this.placeId = placeId; }
	public LocalDateTime getCreatedAt() { return createdAt; }
	public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
	public String getPlaceName() { return placeName; }
	public void setPlaceName(String placeName) { this.placeName = placeName; }
	public String getPlaceRegion() { return placeRegion; }
	public void setPlaceRegion(String placeRegion) { this.placeRegion = placeRegion; }
	public String getPlaceCategory() { return placeCategory; }
	public void setPlaceCategory(String placeCategory) { this.placeCategory = placeCategory; }
	public String getImageUrl() { return imageUrl; }
	public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}

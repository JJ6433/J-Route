package com.blog.dto;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * 리뷰 DTO (reviews 테이블)
 * 여행지별 별점 및 코멘트
 */
@Data
public class ReviewDto {
	private Long reviewId;
	private Long userId;
	private Long placeId;
	private Integer rating;
	private String content;
	private LocalDateTime createdAt;
	private String userNickname;
	private String placeName;

}

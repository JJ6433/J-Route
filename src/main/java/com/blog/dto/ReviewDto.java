package com.blog.dto;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * レビュー DTO (reviews テーブル)
 * 旅行先別評価及びコメント
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

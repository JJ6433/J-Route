package com.blog.dto;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * AI 여행 플래너 DTO (planners 테이블)
 * AI가 생성한 코스 데이터(JSON) 저장
 */
@Data
public class PlannerDto {
	private Long plannerId;
	private Long userId;
	private String title;
	private String planData;
	private boolean isPublic;
	private LocalDateTime createdAt;

}

package com.blog.dto;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * AI 旅行プランナー DTO (planners テーブル)
 * AIが生成したコースデータ(JSON)保存
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

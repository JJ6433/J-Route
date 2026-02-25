package com.blog.dto;

import java.time.LocalDateTime;

/**
 * AI 여행 플래너 DTO (planners 테이블)
 * AI가 생성한 코스 데이터(JSON) 저장
 */
public class PlannerDto {
	private Long plannerId;
	private Long userId;
	private String title;
	private String planData;
	private boolean isPublic;
	private LocalDateTime createdAt;

	public Long getPlannerId() {
		return plannerId;
	}

	public void setPlannerId(Long plannerId) {
		this.plannerId = plannerId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPlanData() {
		return planData;
	}

	public void setPlanData(String planData) {
		this.planData = planData;
	}

	public boolean isPublic() {
		return isPublic;
	}

	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
}

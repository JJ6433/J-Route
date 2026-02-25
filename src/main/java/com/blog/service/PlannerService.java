package com.blog.service;

import com.blog.dto.PlannerDto;
import com.blog.mapper.PlannerMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * AI 旅行プランナーサービス
 * プラン生成(Gemini API連携)、保存、ユーザー別リスト照会
 */
@Service
public class PlannerService {

	private final PlannerMapper plannerMapper;
	private final GeminiService geminiService;
	private final UserService userService;

	public PlannerService(PlannerMapper plannerMapper, GeminiService geminiService, UserService userService) {
		this.plannerMapper = plannerMapper;
		this.geminiService = geminiService;
		this.userService = userService;
	}

	@Transactional(readOnly = true)
	public List<PlannerDto> getByUserId(Long userId) {
		return plannerMapper.findByUserId(userId);
	}

	@Transactional(readOnly = true)
	public PlannerDto getById(Long plannerId) {
		return plannerMapper.findById(plannerId);
	}

	/**
	 * AI 코스 생성 (Google Gemini 연동)
	 */
	public String generatePlan(String region, String days, String style, String companion) {
		// Mockデータの代わりにGemini API呼び出し
		int dayCount = parseDayCount(days);
		return geminiService.getPlan(region, dayCount, style, companion);
	}

	private int parseDayCount(String days) {
		if (days == null || days.isEmpty())
			return 2;
		if (days.contains("1泊2日"))
			return 2;
		if (days.contains("2泊3日"))
			return 3;
		if (days.contains("3泊4日"))
			return 4;
		if (days.contains("4泊5日"))
			return 5;
		return 2;
	}

	@Transactional
	public Long savePlan(Long userId, String title, String planData) {
		PlannerDto dto = new PlannerDto();
		dto.setUserId(userId);
		dto.setTitle(title);
		dto.setPlanData(planData);
		plannerMapper.insertPlanner(dto);
		return dto.getPlannerId();
	}

	@Transactional
	public void updatePlan(Long plannerId, String title, String planData) {
		PlannerDto dto = new PlannerDto();
		dto.setPlannerId(plannerId);
		dto.setTitle(title);
		dto.setPlanData(planData);
		plannerMapper.updatePlanner(dto);
	}

	@Transactional
	public void deletePlan(Long plannerId) {
		plannerMapper.deletePlanner(plannerId);
	}

	// --- Collaborative Features ---

	@Transactional
	public void updatePublicStatus(Long plannerId, boolean isPublic) {
		plannerMapper.updatePublicStatus(plannerId, isPublic);
	}

	@Transactional
	public void inviteCollaborator(Long plannerId, String username) {
		userService.findByUsername(username).ifPresent(user -> {
			com.blog.dto.PlannerCollaboratorDto collab = new com.blog.dto.PlannerCollaboratorDto();
			collab.setPlannerId(plannerId);
			collab.setUserId(user.getUserId());
			collab.setRole("EDITOR");
			plannerMapper.insertCollaborator(collab);
		});
	}

	@Transactional
	public void removeCollaborator(Long plannerId, Long userId) {
		plannerMapper.deleteCollaborator(plannerId, userId);
	}

	@Transactional(readOnly = true)
	public List<com.blog.dto.PlannerCollaboratorDto> getCollaborators(Long plannerId) {
		return plannerMapper.findCollaboratorsByPlannerId(plannerId);
	}

	@Transactional(readOnly = true)
	public boolean canView(Long plannerId, Long userId) {
		PlannerDto planner = plannerMapper.findById(plannerId);
		if (planner == null)
			return false;
		if (planner.isPublic())
			return true;
		if (userId == null)
			return false;
		if (planner.getUserId().equals(userId))
			return true;
		return plannerMapper.checkCollaborator(plannerId, userId) > 0;
	}

	@Transactional(readOnly = true)
	public boolean canEdit(Long plannerId, Long userId) {
		if (userId == null)
			return false;
		PlannerDto planner = plannerMapper.findById(plannerId);
		if (planner == null)
			return false;
		if (planner.getUserId().equals(userId))
			return true;
		return plannerMapper.checkCollaborator(plannerId, userId) > 0;
	}

	@Transactional(readOnly = true)
	public List<PlannerDto> getCollaboratingPlanners(Long userId) {
		return plannerMapper.findCollaboratingPlanners(userId);
	}
}

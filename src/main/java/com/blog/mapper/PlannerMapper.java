package com.blog.mapper;

import com.blog.dto.PlannerCollaboratorDto;
import com.blog.dto.PlannerDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * AI 여행 플래너 MyBatis Mapper
 * 플랜 저장, 사용자별 목록 조회 및 협업 기능
 */
@Mapper
public interface PlannerMapper {

	List<PlannerDto> findByUserId(Long userId);

	PlannerDto findById(Long plannerId);

	void insertPlanner(PlannerDto plannerDto);

	void updatePlanner(PlannerDto plannerDto);

	void deletePlanner(Long plannerId);

	// --- Collaborative Features ---
	void updatePublicStatus(@Param("plannerId") Long plannerId, @Param("isPublic") boolean isPublic);

	void insertCollaborator(PlannerCollaboratorDto collaborator);

	void deleteCollaborator(@Param("plannerId") Long plannerId, @Param("userId") Long userId);

	List<PlannerCollaboratorDto> findCollaboratorsByPlannerId(Long plannerId);

	Integer checkCollaborator(@Param("plannerId") Long plannerId, @Param("userId") Long userId);

	List<PlannerDto> findCollaboratingPlanners(Long userId);
}

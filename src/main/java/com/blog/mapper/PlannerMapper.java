package com.blog.mapper;

import com.blog.dto.PlannerCollaboratorDto;
import com.blog.dto.PlannerDto;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PlannerMapper {

	@Select("SELECT planner_id AS plannerId, user_id AS userId, title, plan_data AS planData, " +
			"is_public AS isPublic, created_at AS createdAt FROM planners WHERE user_id = #{userId} ORDER BY created_at DESC")
	List<PlannerDto> findByUserId(Long userId);

	@Select("SELECT planner_id AS plannerId, user_id AS userId, title, plan_data AS planData, " +
			"is_public AS isPublic, created_at AS createdAt FROM planners WHERE planner_id = #{plannerId}")
	PlannerDto findById(Long plannerId);

	@Insert("INSERT INTO planners (user_id, title, plan_data, is_public) " +
			"VALUES (#{userId}, #{title}, #{planData}, #{isPublic})")
	@Options(useGeneratedKeys = true, keyProperty = "plannerId")
	void insertPlanner(PlannerDto plannerDto);

	@Update("UPDATE planners SET title = #{title}, plan_data = #{planData} WHERE planner_id = #{plannerId}")
	void updatePlanner(PlannerDto plannerDto);

	@Delete("DELETE FROM planners WHERE planner_id = #{plannerId}")
	void deletePlanner(Long plannerId);

	@Update("UPDATE planners SET is_public = #{isPublic} WHERE planner_id = #{plannerId}")
	void updatePublicStatus(@Param("plannerId") Long plannerId, @Param("isPublic") boolean isPublic);

	@Insert("INSERT INTO planner_collaborators (planner_id, user_id, role) VALUES (#{plannerId}, #{userId}, #{role})")
	void insertCollaborator(PlannerCollaboratorDto collaborator);

	@Delete("DELETE FROM planner_collaborators WHERE planner_id = #{plannerId} AND user_id = #{userId}")
	void deleteCollaborator(@Param("plannerId") Long plannerId, @Param("userId") Long userId);

	@Select("SELECT c.collaborator_id AS collaboratorId, c.planner_id AS plannerId, c.user_id AS userId, " +
			"u.username, u.nickname, c.role, c.created_at AS createdAt " +
			"FROM planner_collaborators c JOIN users u ON c.user_id = u.user_id " +
			"WHERE c.planner_id = #{plannerId}")
	List<PlannerCollaboratorDto> findCollaboratorsByPlannerId(Long plannerId);

	@Select("SELECT count(*) FROM planner_collaborators WHERE planner_id = #{plannerId} AND user_id = #{userId}")
	Integer checkCollaborator(@Param("plannerId") Long plannerId, @Param("userId") Long userId);

	@Select("SELECT p.planner_id AS plannerId, p.user_id AS userId, p.title, p.plan_data AS planData, " +
			"p.is_public AS isPublic, p.created_at AS createdAt FROM planners p " +
			"JOIN planner_collaborators c ON p.planner_id = c.planner_id " +
			"WHERE c.user_id = #{userId} ORDER BY p.created_at DESC")
	List<PlannerDto> findCollaboratingPlanners(Long userId);
}

package com.blog.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class PlannerCollaboratorDto {
    private Long collaboratorId;
    private Long plannerId;
    private Long userId;
    private String username; // Join for convenience
    private String nickname; // Join for convenience
    private String role;
    private LocalDateTime createdAt;
    

}

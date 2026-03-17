package com.blog.dto;

import lombok.Data;
import java.sql.Timestamp;

/**
 * 여행 예산 저장용 DTO (budgets 테이블)
 */
@Data // Getter, Setter, toString 등 자동 생성
public class BudgetDto {
    private Long budgetId;
    private Long userId;          // 로그인한 유저 ID
    
    private int transportCost;    // 항공/교통비
    private int hotelCost;        // 숙박비
    private int foodCost;         // 식비(맛집/카페)
    
    private Timestamp updatedAt;  // 마지막 저장 시간
}
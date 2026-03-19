package com.blog.dto;

import lombok.Data;
import java.sql.Timestamp;

/**
 * 旅行予算保存用 DTO (budgets テーブル)
 */
@Data // Getter, Setter等自動生成
public class BudgetDto {
    private Long budgetId;
    private Long userId;          // ログインユーザーID
    
    private int transportCost;    // 航空/交通費
    private int hotelCost;        // 宿泊費
    private int foodCost;         // 食費
    
    private Timestamp updatedAt;  // 最終保存時間
}
package com.blog.mapper;

import com.blog.dto.BudgetDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BudgetMapper {
    // 予算照会
    BudgetDto getBudgetByUserId(Long userId);
    
    // 予算保存
    void saveOrUpdateBudget(BudgetDto budgetDto);
}
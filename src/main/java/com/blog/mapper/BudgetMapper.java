package com.blog.mapper;

import com.blog.dto.BudgetDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BudgetMapper {
    // 특정 유저의 예산 불러오기
    BudgetDto getBudgetByUserId(Long userId);
    
    // 예산 저장하기 (없으면 새로 만들고, 있으면 덮어쓰기!)
    void saveOrUpdateBudget(BudgetDto budgetDto);
}
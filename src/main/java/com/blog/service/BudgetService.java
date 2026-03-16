package com.blog.service;

import com.blog.dto.BudgetDto;
import com.blog.mapper.BudgetMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BudgetService {

    private final BudgetMapper budgetMapper;

    public BudgetService(BudgetMapper budgetMapper) {
        this.budgetMapper = budgetMapper;
    }

    // 내 예산 가져오기
    public BudgetDto getMyBudget(Long userId) {
        return budgetMapper.getBudgetByUserId(userId);
    }

    // 예산 저장하기
    @Transactional
    public void saveMyBudget(BudgetDto budgetDto) {
        budgetMapper.saveOrUpdateBudget(budgetDto);
    }
}
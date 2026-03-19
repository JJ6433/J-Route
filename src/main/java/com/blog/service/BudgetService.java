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

    // 予算照会
    public BudgetDto getMyBudget(Long userId) {
        return budgetMapper.getBudgetByUserId(userId);
    }

    // 予算保存
    @Transactional
    public void saveMyBudget(BudgetDto budgetDto) {
        budgetMapper.saveOrUpdateBudget(budgetDto);
    }
}
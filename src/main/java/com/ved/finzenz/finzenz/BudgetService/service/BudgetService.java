package com.ved.finzenz.finzenz.BudgetService.service;

import com.ved.finzenz.finzenz.BudgetService.dto.BudgetResponse;
import com.ved.finzenz.finzenz.BudgetService.entity.Budget;
import com.ved.finzenz.finzenz.BudgetService.request.BudgetRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface BudgetService {
    BudgetResponse createBudget(BudgetRequest budgetRequest);
    BudgetResponse updateBudget(Integer id, BudgetRequest budgetRequest);
    void deleteBudget(Integer id);
    List<BudgetResponse> getBudgetsByUserId(Integer userId);
    List<BudgetResponse> getBudgetsByCategory(Integer userId, String category);
    BigDecimal getRemainingBudget(Integer userId, String category, LocalDate date);
}

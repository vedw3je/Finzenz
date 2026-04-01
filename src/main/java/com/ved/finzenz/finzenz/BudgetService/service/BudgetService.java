package com.ved.finzenz.finzenz.BudgetService.service;

import com.ved.finzenz.finzenz.BudgetService.entity.Budget;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface BudgetService {
    Budget createBudget(Budget budget);
    Budget updateBudget(Integer id, Budget budget);
    void deleteBudget(Integer id);
    List<Budget> getBudgetsByUserId(Integer userId);
    List<Budget> getBudgetsByCategory(Integer userId, String category);
    BigDecimal getRemainingBudget(Integer userId, String category, LocalDate date);
}

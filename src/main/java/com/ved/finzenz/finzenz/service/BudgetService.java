package com.ved.finzenz.finzenz.service;

import com.ved.finzenz.finzenz.entities.Budget;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface BudgetService {
    Budget createBudget(Budget budget);
    Budget updateBudget(Integer id, Budget budget);
    void deleteBudget(Integer id);
    List<Budget> getBudgetsByUserId(Integer userId);
    List<Budget> getBudgetsByCategory(Integer userId, String category);
    BigDecimal getRemainingBudget(Integer userId, String category, LocalDate date);
}

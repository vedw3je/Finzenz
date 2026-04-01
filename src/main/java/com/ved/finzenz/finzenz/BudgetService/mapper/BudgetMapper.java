package com.ved.finzenz.finzenz.BudgetService.mapper;

import com.ved.finzenz.finzenz.BudgetService.dto.BudgetResponse;
import com.ved.finzenz.finzenz.BudgetService.entity.Budget;
import com.ved.finzenz.finzenz.BudgetService.request.BudgetRequest;
import com.ved.finzenz.finzenz.UserService.entity.User;
import org.springframework.stereotype.Component;

@Component
public class BudgetMapper {

    public Budget toEntity(BudgetRequest request, User user) {
        return Budget.builder()
                .user(user)
                .category(request.getCategory())
                .amount(request.getAmount())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .build();
    }

    public BudgetResponse toResponse(Budget budget) {
        return BudgetResponse.builder()
                .id(budget.getId())
                .userId(budget.getUser().getId())
                .category(budget.getCategory())
                .amount(budget.getAmount())
                .startDate(budget.getStartDate())
                .endDate(budget.getEndDate())
                .build();
    }
}
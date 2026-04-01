package com.ved.finzenz.finzenz.BudgetService.dto;

import com.ved.finzenz.finzenz.BudgetService.entity.Budget;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BudgetResponse {
    private Integer id;
    private Long userId;
    private String category;
    private BigDecimal amount;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime createdDate;


    public BudgetResponse(Budget budget) {
        this.id = budget.getId();
        this.userId = budget.getUser().getId();
        this.category = budget.getCategory();
        this.amount = budget.getAmount();
        this.startDate = budget.getStartDate();
        this.endDate = budget.getEndDate();
        this.createdDate = budget.getCreatedAt();
    }
}

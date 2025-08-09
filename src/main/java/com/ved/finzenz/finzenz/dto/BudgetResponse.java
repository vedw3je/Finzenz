package com.ved.finzenz.finzenz.dto;

import com.ved.finzenz.finzenz.entities.Budget;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BudgetResponse {
    private Integer id;
    private Integer userId;
    private String category;
    private BigDecimal amount;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate createdDate;


    public BudgetResponse(Budget budget) {
        this.id = budget.getId();
        this.userId = budget.getUserId();
        this.category = budget.getCategory();
        this.amount = budget.getAmount();
        this.startDate = budget.getStartDate();
        this.endDate = budget.getEndDate();
        this.createdDate = budget.getCreatedAt();
    }
}

package com.ved.finzenz.finzenz.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BudgetRequest {
    private Integer userId;
    private String category;
    private BigDecimal amount;
    private LocalDate startDate;
    private LocalDate endDate;
}
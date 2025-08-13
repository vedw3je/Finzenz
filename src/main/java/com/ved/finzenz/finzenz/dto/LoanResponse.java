package com.ved.finzenz.finzenz.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoanResponse {
    private Long loanId;
    private Long accountId;
    private BigDecimal loanAmount;
    private BigDecimal interestRate;
    private int termInDays;
    private BigDecimal emiAmount;
    private Integer recurringDays;
    private BigDecimal remainingBalance;
    private LocalDateTime startDate;
    private LocalDateTime nextPaymentDate;
    private boolean active;
}

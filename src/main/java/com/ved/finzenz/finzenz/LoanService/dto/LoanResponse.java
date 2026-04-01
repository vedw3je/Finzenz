package com.ved.finzenz.finzenz.LoanService.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
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


    @NotNull(message = "Loan amount cannot be null")
    @DecimalMin(value = "0.01", message = "Loan amount must be greater than zero")
    private BigDecimal loanAmount;


    @NotNull(message = "Interest rate cannot be null")
    @DecimalMin(value = "0.00", message = "Interest rate cannot be negative")
    private BigDecimal interestRate;


    private int termInDays;

    @NotNull(message = "EMI amount cannot be null")
    @DecimalMin(value = "0.01", message = "EMI amount must be greater than zero")
    private BigDecimal emiAmount;


    private Integer recurringDays;
    private BigDecimal remainingBalance;

    @NotNull(message = "Start date cannot be null")
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;


    private LocalDateTime nextPaymentDate;
    private boolean active;
}

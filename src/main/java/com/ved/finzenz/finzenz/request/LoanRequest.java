package com.ved.finzenz.finzenz.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoanRequest {

    @NotNull(message = "Account ID cannot be null")
    private Long accountId;

    @NotBlank(message = "Lender name cannot be blank")
    private String lenderName;

    @NotNull(message = "Loan amount cannot be null")
    @DecimalMin(value = "0.01", message = "Loan amount must be greater than zero")
    private BigDecimal loanAmount;

    @NotNull(message = "Interest rate cannot be null")
    @DecimalMin(value = "0.00", message = "Interest rate cannot be negative")
    private BigDecimal interestRate;

    @NotNull(message = "Term in days cannot be null")
    @Min(value = 1, message = "Term must be at least 1 day")
    private Integer termInDays;

    private LocalDate startDate; // Optional, defaults to today

    private LocalDate endDate; // Optional, calculated from start date + term

    @NotNull(message = "Recurring interval is required")
//    @Min(value = 1, message = "Recurring interval must be at least 1 day")
    private Integer recurringIntervalDays; // Mandatory: 1=daily, 7=weekly, 15=bi-weekly, 30=monthly


    // Helper method to calculate total installments
    public Integer calculateTotalInstallments() {
        if (termInDays == null || recurringIntervalDays == null) {
            return null;
        }
        return (int) Math.ceil((double) termInDays / recurringIntervalDays);
    }

    // Validation method
    public boolean isValidDateRange() {
        if (startDate == null || endDate == null) return true; // Will be calculated
        return !endDate.isBefore(startDate);
    }

    // Helper to validate that term matches the calculated period
    public boolean isTermConsistent() {
        if (startDate == null || endDate == null || termInDays == null) return true;

        long actualDays = startDate.until(endDate).getDays();
        return Math.abs(actualDays - termInDays) <= 1; // Allow 1 day difference for rounding
    }
}
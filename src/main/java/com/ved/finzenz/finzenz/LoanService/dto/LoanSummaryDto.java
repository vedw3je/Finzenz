package com.ved.finzenz.finzenz.LoanService.dto;

import com.ved.finzenz.finzenz.LoanService.entity.Loan;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class LoanSummaryDto {
    private Long loanId;

    @NotNull(message = "Lender name cannot be null")
    private String lender;

    @NotNull(message = "EMI amount cannot be null")
    @DecimalMin(value = "0.01", message = "EMI amount must be greater than zero")
    private BigDecimal emiAmount;


    private LocalDate nextDueDate;
    private Loan.LoanStatus status;
    private BigDecimal outstandingAmount;
}

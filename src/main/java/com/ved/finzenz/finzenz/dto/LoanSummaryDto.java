package com.ved.finzenz.finzenz.dto;

import com.ved.finzenz.finzenz.entities.Loan;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class LoanSummaryDto {
    private Long loanId;
    private String lender;
    private BigDecimal emiAmount;
    private LocalDate nextDueDate;
    private Loan.LoanStatus status;
    private BigDecimal outstandingAmount;
}

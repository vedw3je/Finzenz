package com.ved.finzenz.finzenz.service;

import com.ved.finzenz.finzenz.dto.LoanSummaryDto;
import com.ved.finzenz.finzenz.dto.UpcomingEmiDto;
import com.ved.finzenz.finzenz.entities.Loan;

import java.math.BigDecimal;
import java.util.List;

public interface LoanService {

    Loan createLoan(Loan loan);

    /**
     * Get loan summary for a user
     */
    List<LoanSummaryDto> getLoanSummary(Long userId);

    List<Loan> getLoanSummaryByUser(Long userId);

    /**
     * Get upcoming EMIs for a user
     */
    List<UpcomingEmiDto> getUpcomingEmis(Long userId);

    /**
     * Record an EMI payment
     */
    void recordEmiPayment(Long loanId);

    /**
     * Calculate outstanding amount for a loan
     */
    BigDecimal calculateOutstanding(Loan loan);

    /**
     * Get overdue loans for a user
     */
    List<Loan> getOverdueLoans(Long userId);

    /**
     * Get total outstanding amount for a user
     */
    BigDecimal getTotalOutstanding(Long userId);

    Loan getLoanById(Long loanId);
}

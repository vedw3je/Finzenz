package com.ved.finzenz.finzenz.LoanService.service;

import com.ved.finzenz.finzenz.LoanService.dto.LoanResponse;
import com.ved.finzenz.finzenz.LoanService.dto.LoanSummaryDto;
import com.ved.finzenz.finzenz.LoanService.dto.UpcomingEmiDto;
import com.ved.finzenz.finzenz.LoanService.entity.Loan;
import com.ved.finzenz.finzenz.LoanService.request.LoanRequest;

import java.math.BigDecimal;
import java.util.List;

public interface LoanService {

    LoanSummaryDto createLoan(LoanRequest loanRequest);

    /**
     * Get loan summary for a user
     */
    List<LoanSummaryDto> getLoanSummary(Long userId);

    List<LoanSummaryDto> getLoanSummaryByUser(Long userId);

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
    List<LoanResponse> getOverdueLoans(Long userId);

    /**
     * Get total outstanding amount for a user
     */
    BigDecimal getTotalOutstanding(Long userId);

    Loan getLoanById(Long loanId);
}

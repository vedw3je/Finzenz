package com.ved.finzenz.finzenz.LoanService.mapper;

import com.ved.finzenz.finzenz.AccountService.entity.Account;
import com.ved.finzenz.finzenz.LoanService.dto.LoanResponse;
import com.ved.finzenz.finzenz.LoanService.dto.LoanSummaryDto;
import com.ved.finzenz.finzenz.LoanService.dto.UpcomingEmiDto;
import com.ved.finzenz.finzenz.LoanService.entity.Loan;
import com.ved.finzenz.finzenz.LoanService.request.LoanRequest;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class LoanMapper {

    // ---------------- REQUEST → ENTITY ----------------
    public Loan toEntity(LoanRequest request, Account account) {

        LocalDate startDate = request.getStartDate() != null
                ? request.getStartDate()
                : LocalDate.now();

        LocalDate endDate = request.getEndDate() != null
                ? request.getEndDate()
                : startDate.plusDays(request.getTermInDays());

        Integer totalInstallments = request.calculateTotalInstallments();

        return Loan.builder()
                .account(account)
                .lenderName(request.getLenderName())
                .principalAmount(request.getLoanAmount())
                .interestRate(request.getInterestRate())
                .startDate(startDate)
                .endDate(endDate)
                .recurringIntervalDays(request.getRecurringIntervalDays())
                .totalInstallments(totalInstallments)
                .status(Loan.LoanStatus.ACTIVE)
                .completedInstallments(0)
                .build();
    }

    // ---------------- ENTITY → FULL RESPONSE ----------------
    public LoanResponse toResponse(Loan loan, BigDecimal outstandingAmount) {

        LoanResponse response = new LoanResponse();

        response.setLoanId(loan.getId());
        response.setAccountId(loan.getAccount().getId());
        response.setLoanAmount(loan.getPrincipalAmount());
        response.setInterestRate(loan.getInterestRate());

        // term in days (calculated)
        int termDays = (int) loan.getStartDate().until(loan.getEndDate()).getDays();
        response.setTermInDays(termDays);

        response.setEmiAmount(loan.getEmiAmount());
        response.setRecurringDays(loan.getRecurringIntervalDays());
        response.setRemainingBalance(outstandingAmount);

        response.setStartDate(loan.getStartDate().atStartOfDay());

        response.setNextPaymentDate(
                loan.getNextDueDate() != null
                        ? loan.getNextDueDate().atStartOfDay()
                        : null
        );

        response.setActive(loan.getStatus() == Loan.LoanStatus.ACTIVE);

        return response;
    }

    // ---------------- ENTITY → SUMMARY ----------------
    public LoanSummaryDto toSummary(Loan loan, BigDecimal outstandingAmount) {

        return LoanSummaryDto.builder()
                .loanId(loan.getId())
                .lender(loan.getLenderName())
                .emiAmount(loan.getEmiAmount())
                .nextDueDate(loan.getNextDueDate())
                .status(loan.getStatus())
                .outstandingAmount(outstandingAmount)
                .build();
    }

    // ---------------- ENTITY → UPCOMING EMI ----------------
    public UpcomingEmiDto toUpcomingEmi(Loan loan) {

        return UpcomingEmiDto.builder()
                .loanId(loan.getId())
                .lender(loan.getLenderName())
                .amount(loan.getEmiAmount())
                .dueDate(loan.getNextDueDate())
                .build();
    }
}
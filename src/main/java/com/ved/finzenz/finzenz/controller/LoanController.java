package com.ved.finzenz.finzenz.controller;


import com.ved.finzenz.finzenz.dto.LoanSummaryDto;
import com.ved.finzenz.finzenz.entities.Loan;
import com.ved.finzenz.finzenz.request.LoanRequest;
import com.ved.finzenz.finzenz.service.LoanService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    // ------------------ LOAN ENDPOINTS ------------------

    @PostMapping
    public ResponseEntity<?> createLoanRecord(@Valid @RequestBody LoanRequest loanRequest) {
        try {
            if (loanRequest.getStartDate() != null && loanRequest.getEndDate() != null) {
                if (!loanRequest.isValidDateRange()) {
                    return ResponseEntity.badRequest()
                            .body(Map.of("error", "End date cannot be before start date"));
                }
            }

            Loan loan = convertToLoanEntity(loanRequest);
            Loan createdLoan = loanService.createLoan(loan);

            LoanSummaryDto response = LoanSummaryDto.builder()
                    .loanId(createdLoan.getId())
                    .lender(createdLoan.getLenderName())
                    .emiAmount(createdLoan.getEmiAmount())
                    .nextDueDate(createdLoan.getNextDueDate())
                    .status(createdLoan.getStatus())
                    .outstandingAmount(loanService.calculateOutstanding(createdLoan))
                    .build();

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred: " + e.getMessage()));
        }
    }

    @GetMapping("/{loanId}")
    public ResponseEntity<?> getLoanDetails(@PathVariable Long loanId) {
        try {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                    .body(Map.of("message", "Please implement getLoanById method in service"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/account/{accountId}/summary")
    public ResponseEntity<?> getLoanSummary(@PathVariable Long accountId) {
        try {
            return ResponseEntity.ok(loanService.getLoanSummary(accountId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred: " + e.getMessage()));
        }
    }

    @GetMapping("/account/{accountId}/upcoming")
    public ResponseEntity<?> getUpcomingEmis(@PathVariable Long accountId) {
        try {
            return ResponseEntity.ok(loanService.getUpcomingEmis(accountId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred: " + e.getMessage()));
        }
    }

    @PostMapping("/emi/{loanId}/pay")
    public ResponseEntity<?> recordEmiPayment(@PathVariable Long loanId) {
        try {
            loanService.recordEmiPayment(loanId);
            return ResponseEntity.ok(Map.of("message", "EMI payment recorded successfully"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred: " + e.getMessage()));
        }
    }

    @GetMapping("/account/{accountId}/overdue")
    public ResponseEntity<?> getOverdueLoans(@PathVariable Long accountId) {
        try {
            return ResponseEntity.ok(loanService.getOverdueLoans(accountId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred: " + e.getMessage()));
        }
    }

    @GetMapping("/account/{accountId}/total-outstanding")
    public ResponseEntity<?> getTotalOutstanding(@PathVariable Long accountId) {
        try {
            BigDecimal totalOutstanding = loanService.getTotalOutstanding(accountId);
            return ResponseEntity.ok(Map.of("totalOutstanding", totalOutstanding));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred: " + e.getMessage()));
        }
    }

    // ------------------ HELPER ------------------

    private Loan convertToLoanEntity(LoanRequest loanRequest) {
        LocalDate startDate = loanRequest.getStartDate() != null
                ? loanRequest.getStartDate()
                : LocalDate.now();

        LocalDate endDate = loanRequest.getEndDate() != null
                ? loanRequest.getEndDate()
                : startDate.plusDays(loanRequest.getTermInDays());

        int totalInstallments = (int) Math.ceil(
                (double) loanRequest.getTermInDays() / loanRequest.getRecurringIntervalDays());

        return Loan.builder()
                .accountId(loanRequest.getAccountId())
                .lenderName(loanRequest.getLenderName())
                .principalAmount(loanRequest.getLoanAmount())
                .interestRate(loanRequest.getInterestRate())
                .startDate(startDate)
                .endDate(endDate)
                .recurringIntervalDays(loanRequest.getRecurringIntervalDays())
                .totalInstallments(totalInstallments)
                .status(Loan.LoanStatus.ACTIVE)
                .completedInstallments(0)
                .build();
    }
}

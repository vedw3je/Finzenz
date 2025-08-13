package com.ved.finzenz.finzenz.controller;



import com.ved.finzenz.finzenz.dto.LoanSummaryDto;
import com.ved.finzenz.finzenz.dto.TransactionResponse;
import com.ved.finzenz.finzenz.entities.Loan;
import com.ved.finzenz.finzenz.entities.Transaction;
import com.ved.finzenz.finzenz.request.LoanRequest;
import com.ved.finzenz.finzenz.request.TransactionRequest;
import com.ved.finzenz.finzenz.service.LoanService;
import com.ved.finzenz.finzenz.service.TransactionService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final LoanService loanService;

    // ---------------- Create Transaction ----------------
    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(@Valid @RequestBody TransactionRequest request) {
        Transaction transaction = new Transaction();
        transaction.setAccountId(request.getAccountId());
        transaction.setAmount(request.getAmount());
        transaction.setTransactionDate(request.getTransactionDate() != null ? request.getTransactionDate() : LocalDateTime.now());
        transaction.setDescription(request.getDescription());
        transaction.setTransactionType(request.getTransactionType());
        transaction.setCategory(request.getCategory());

        Transaction created = transactionService.createTransaction(transaction);
        return ResponseEntity.status(201).body(new TransactionResponse(created));
    }

    // ---------------- Get Transactions for a User ----------------
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByUserId(@PathVariable Integer userId) {
        List<TransactionResponse> responses = transactionService.getTransactionsByUserId(userId)
                .stream()
                .map(TransactionResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    // ---------------- Update Transaction ----------------
    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponse> updateTransaction(@PathVariable Integer id,
                                                                 @Valid @RequestBody TransactionRequest request) {
        Transaction transaction = new Transaction();
        transaction.setAccountId(request.getAccountId());
        transaction.setAmount(request.getAmount());
        transaction.setTransactionDate(request.getTransactionDate() != null ? request.getTransactionDate() : LocalDateTime.now());
        transaction.setDescription(request.getDescription());
        transaction.setTransactionType(request.getTransactionType());
        transaction.setCategory(request.getCategory());

        Transaction updated = transactionService.updateTransaction(id, transaction);
        return ResponseEntity.ok(new TransactionResponse(updated));
    }

    // ---------------- Delete Transaction ----------------
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Integer id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/user/{userId}/date-range")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByUserIdAndDateRange(
            @PathVariable Integer userId,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        // Convert LocalDate to LocalDateTime at start and end of day
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        List<TransactionResponse> responses = transactionService
                .getTransactionsByUserIdAndDateRange(userId, startDateTime, endDateTime)
                .stream()
                .map(TransactionResponse::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/user/{userId}/spending")
    public ResponseEntity<BigDecimal> getTotalSpendingByCategoryForUser(
            @PathVariable Integer userId,
            @RequestParam("category") String category) {

        BigDecimal totalSpending = transactionService.getTotalSpendingByCategoryForUser(userId, category);
        return ResponseEntity.ok(totalSpending);
    }

    @GetMapping("/user/{userId}/income")
    public ResponseEntity<BigDecimal> getTotalIncomeForUser(@PathVariable Integer userId) {
        BigDecimal totalIncome = transactionService.getTotalIncomeForUser(userId);
        return ResponseEntity.ok(totalIncome);
    }

    @GetMapping("/user/{userId}/expense")
    public ResponseEntity<BigDecimal> getTotalExpenseForUser(@PathVariable Integer userId) {
        BigDecimal totalExpense = transactionService.getTotalExpenseForUser(userId);
        return ResponseEntity.ok(totalExpense);
    }

    @GetMapping("/user/{userId}/search")
    public ResponseEntity<List<TransactionResponse>> searchTransactionsByDescriptionForUser(
            @PathVariable Integer userId,
            @RequestParam("keyword") String keyword) {

        List<TransactionResponse> responses = transactionService
                .searchTransactionsByDescriptionForUser(userId, keyword)
                .stream()
                .map(TransactionResponse::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    // ------------------LOAN ENDPOINTS------------------
    @PostMapping("/loan")
    public ResponseEntity<?> createLoanRecord(@Valid @RequestBody LoanRequest loanRequest) {
        try {
            // Validate dates if provided
            if (loanRequest.getStartDate() != null && loanRequest.getEndDate() != null) {
                if (!loanRequest.isValidDateRange()) {
                    return ResponseEntity.badRequest()
                            .body(Map.of("error", "End date cannot be before start date"));
                }
            }

            // Convert LoanRequest to Loan entity
            Loan loan = convertToLoanEntity(loanRequest);

            // Create loan using the service
            Loan createdLoan = loanService.createLoan(loan);

            // Convert to LoanSummaryDto for response
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
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred: " + e.getMessage()));
        }
    }

    @GetMapping("/loan/{loanId}")
    public ResponseEntity<?> getLoanDetails(@PathVariable Long loanId) {
        try {
            // You might want to add a getLoanById method to your service
            // For now, assuming you have access to loanRepository
            // Loan loan = loanRepository.findById(loanId)
            //     .orElseThrow(() -> new EntityNotFoundException("Loan not found"));

            // LoanSummaryDto response = LoanSummaryDto.builder()
            //         .loanId(loan.getId())
            //         .lender(loan.getLenderName())
            //         .emiAmount(loan.getEmiAmount())
            //         .nextDueDate(loan.getNextDueDate())
            //         .status(loan.getStatus())
            //         .outstandingAmount(loanService.calculateOutstanding(loan))
            //         .build();

            // return ResponseEntity.ok(response);

            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                    .body(Map.of("message", "Please implement getLoanById method in service"));

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}/summary")
    public ResponseEntity<?> getLoanSummary(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(loanService.getLoanSummary(userId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred: " + e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}/upcoming")
    public ResponseEntity<?> getUpcomingEmis(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(loanService.getUpcomingEmis(userId));
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
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred: " + e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}/overdue")
    public ResponseEntity<?> getOverdueLoans(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(loanService.getOverdueLoans(userId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred: " + e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}/total-outstanding")
    public ResponseEntity<?> getTotalOutstanding(@PathVariable Long userId) {
        try {
            BigDecimal totalOutstanding = loanService.getTotalOutstanding(userId);
            return ResponseEntity.ok(Map.of("totalOutstanding", totalOutstanding));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred: " + e.getMessage()));
        }
    }

    /**
     * Converts LoanRequest DTO to Loan entity
     */
    private Loan convertToLoanEntity(LoanRequest loanRequest) {
        // Set default dates
        LocalDate startDate = loanRequest.getStartDate() != null ?
                loanRequest.getStartDate() : LocalDate.now();
        LocalDate endDate = loanRequest.getEndDate() != null ?
                loanRequest.getEndDate() : startDate.plusDays(loanRequest.getTermInDays());

        // Calculate total installments
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

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




}

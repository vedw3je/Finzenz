package com.ved.finzenz.finzenz.TransactionService.controller;



import com.ved.finzenz.finzenz.TransactionService.dto.TransactionResponse;
import com.ved.finzenz.finzenz.TransactionService.entity.Transaction;
import com.ved.finzenz.finzenz.TransactionService.request.TransactionRequest;
import com.ved.finzenz.finzenz.LoanService.service.LoanService;
import com.ved.finzenz.finzenz.TransactionService.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
@Tag(name = "Transaction APIs", description = "Manage financial transactions, filters, and analytics")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    // ---------------- CREATE ----------------
    @Operation(
            summary = "Create a transaction",
            description = "Allows ADMIN to create a debit or credit transaction and update account balance"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Transaction created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(
            @Valid @RequestBody TransactionRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(transactionService.createTransaction(request));
    }

    // ---------------- GET BY ID ----------------
    @Operation(
            summary = "Get transaction by ID",
            description = "Fetch details of a specific transaction"
    )
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST','VIEWER')")
    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getTransactionById(
            @PathVariable Integer id) {

        return ResponseEntity.ok(
                transactionService.getTransactionById(id)
        );
    }

    // ---------------- GET BY USER ----------------
    @Operation(
            summary = "Get transactions by user",
            description = "Returns all transactions associated with a user"
    )
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST','VIEWER')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByUser(
            @PathVariable Integer userId) {

        return ResponseEntity.ok(
                transactionService.getTransactionsByUser(userId)
        );
    }

    // ---------------- GET BY ACCOUNT ----------------
    @Operation(
            summary = "Get transactions by account",
            description = "Fetch transactions linked to a specific account"
    )
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST','VIEWER')")
    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByAccount(
            @PathVariable Long accountId) {

        return ResponseEntity.ok(
                transactionService.getTransactionsByAccountId(accountId)
        );
    }

    // ---------------- UPDATE ----------------
    @Operation(
            summary = "Update a transaction",
            description = "Allows ADMIN to modify transaction details"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transaction updated successfully"),
            @ApiResponse(responseCode = "404", description = "Transaction not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponse> updateTransaction(
            @PathVariable Integer id,
            @Valid @RequestBody TransactionRequest request) {

        return ResponseEntity.ok(
                transactionService.updateTransaction(id, request)
        );
    }

    // ---------------- DELETE ----------------
    @Operation(
            summary = "Delete a transaction",
            description = "Allows ADMIN to delete a transaction permanently"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Transaction deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Transaction not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Integer id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }

    // ---------------- DATE RANGE ----------------
    @Operation(
            summary = "Get transactions by date range",
            description = "Fetch transactions for a user within a specified date range"
    )
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST','VIEWER')")
    @GetMapping("/user/{userId}/date-range")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByDateRange(
            @PathVariable Integer userId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {

        return ResponseEntity.ok(
                transactionService.getTransactionsByUserIdAndDateRange(
                        userId,
                        startDate.atStartOfDay(),
                        endDate.atTime(LocalTime.MAX)
                )
        );
    }

    // ---------------- SEARCH ----------------
    @Operation(
            summary = "Search transactions",
            description = "Search transactions by description keyword"
    )
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST','VIEWER')")
    @GetMapping("/user/{userId}/search")
    public ResponseEntity<List<TransactionResponse>> searchTransactions(
            @PathVariable Integer userId,
            @RequestParam String keyword) {

        return ResponseEntity.ok(
                transactionService.searchTransactionsByDescriptionForUser(userId, keyword)
        );
    }

    // ---------------- MONTHLY ----------------
    @Operation(
            summary = "Get monthly transactions",
            description = "Returns transactions for a specific month and year"
    )
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST','VIEWER')")
    @GetMapping("/user/{userId}/monthly")
    public ResponseEntity<List<TransactionResponse>> getMonthlyTransactions(
            @PathVariable Integer userId,
            @RequestParam int month,
            @RequestParam int year) {

        return ResponseEntity.ok(
                transactionService.getUserMonthlyTransactions(userId, month, year)
        );
    }

    // ---------------- DASHBOARD APIs ----------------

    @Operation(
            summary = "Get spending by category",
            description = "Returns total spending for a given category (ANALYST & ADMIN)"
    )
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
    @GetMapping("/user/{userId}/spending")
    public ResponseEntity<BigDecimal> getSpendingByCategory(
            @PathVariable Integer userId,
            @RequestParam String category) {

        return ResponseEntity.ok(
                transactionService.getTotalSpendingByCategory(userId, category)
        );
    }

    @Operation(
            summary = "Get total income",
            description = "Returns total income for a user (ANALYST & ADMIN)"
    )
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
    @GetMapping("/user/{userId}/income")
    public ResponseEntity<BigDecimal> getTotalIncome(@PathVariable Integer userId) {
        return ResponseEntity.ok(
                transactionService.getTotalIncome(userId)
        );
    }

    @Operation(
            summary = "Get total expense",
            description = "Returns total expenses for a user (ANALYST & ADMIN)"
    )
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
    @GetMapping("/user/{userId}/expense")
    public ResponseEntity<BigDecimal> getTotalExpense(@PathVariable Integer userId) {
        return ResponseEntity.ok(
                transactionService.getTotalExpense(userId)
        );
    }


    @Operation(
            summary = "Get paginated transactions",
            description = "Fetch transactions with pagination support (page, size, sort)"
    )
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST','VIEWER')")
    @GetMapping("/user/{userId}/paginated")
    public ResponseEntity<Page<TransactionResponse>> getTransactionsPaginated(
            @PathVariable Integer userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "transactionDate") String sortBy
    ) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());

        return ResponseEntity.ok(
                transactionService.getTransactionsPaginated(userId, pageable)
        );
    }

    @Operation(
            summary = "Get net balance",
            description = "Calculates net balance = total income - total expense"
    )
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
    @GetMapping("/user/{userId}/net-balance")
    public ResponseEntity<BigDecimal> getNetBalance(@PathVariable Integer userId) {

        return ResponseEntity.ok(
                transactionService.getNetBalance(userId)
        );
    }
}
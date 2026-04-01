package com.ved.finzenz.finzenz.LoanService.controller;


import com.ved.finzenz.finzenz.LoanService.dto.LoanResponse;
import com.ved.finzenz.finzenz.LoanService.dto.LoanSummaryDto;
import com.ved.finzenz.finzenz.LoanService.dto.UpcomingEmiDto;
import com.ved.finzenz.finzenz.LoanService.entity.Loan;
import com.ved.finzenz.finzenz.LoanService.mapper.LoanMapper;
import com.ved.finzenz.finzenz.LoanService.request.LoanRequest;
import com.ved.finzenz.finzenz.LoanService.service.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Tag(name = "Loan APIs", description = "Manage loans, EMIs, and outstanding liabilities")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;
    private final LoanMapper loanMapper;

    // ---------------- CREATE LOAN ----------------
    @Operation(
            summary = "Create a loan",
            description = "Allows ADMIN to create a new loan with EMI schedule and repayment details"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Loan created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<LoanSummaryDto> createLoan(
            @Valid @RequestBody LoanRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(loanService.createLoan(request));
    }

    // ---------------- GET LOAN BY ID ----------------
    @Operation(
            summary = "Get loan details",
            description = "Fetch complete loan details including outstanding amount"
    )
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST','VIEWER')")
    @GetMapping("/{loanId}")
    public ResponseEntity<LoanResponse> getLoan(@PathVariable Long loanId) {

        Loan loan = loanService.getLoanById(loanId);

        return ResponseEntity.ok(
                loanMapper.toResponse(
                        loan,
                        loanService.calculateOutstanding(loan)
                )
        );
    }

    // ---------------- GET LOANS BY USER ----------------
    @Operation(
            summary = "Get loans by user",
            description = "Returns all loans associated with a specific user"
    )
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST','VIEWER')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<LoanSummaryDto>> getLoansByUser(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                loanService.getLoanSummaryByUser(userId)
        );
    }

    // ---------------- GET LOANS BY ACCOUNT ----------------
    @Operation(
            summary = "Get loans by account",
            description = "Fetch all loans linked to a specific account"
    )
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST','VIEWER')")
    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<LoanSummaryDto>> getLoansByAccount(
            @PathVariable Long accountId) {

        return ResponseEntity.ok(
                loanService.getLoanSummary(accountId)
        );
    }

    // ---------------- UPCOMING EMIs ----------------
    @Operation(
            summary = "Get upcoming EMIs",
            description = "Returns list of upcoming EMI payments for a user"
    )
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST','VIEWER')")
    @GetMapping("/user/{userId}/upcoming")
    public ResponseEntity<List<UpcomingEmiDto>> getUpcomingEmis(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                loanService.getUpcomingEmis(userId)
        );
    }

    // ---------------- PAY EMI ----------------
    @Operation(
            summary = "Pay EMI",
            description = "Allows ADMIN to record EMI payment and update loan status"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "EMI payment recorded"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Loan not found")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{loanId}/pay")
    public ResponseEntity<Void> payEmi(@PathVariable Long loanId) {

        loanService.recordEmiPayment(loanId);
        return ResponseEntity.ok().build();
    }

    // ---------------- OVERDUE LOANS ----------------
    @Operation(
            summary = "Get overdue loans",
            description = "Returns loans that have missed EMI payments"
    )
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST','VIEWER')")
    @GetMapping("/user/{userId}/overdue")
    public ResponseEntity<List<LoanResponse>> getOverdueLoans(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                loanService.getOverdueLoans(userId)
        );
    }

    // ---------------- TOTAL OUTSTANDING ----------------
    @Operation(
            summary = "Get total outstanding",
            description = "Calculates total outstanding loan amount for a user"
    )
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST','VIEWER')")
    @GetMapping("/user/{userId}/outstanding")
    public ResponseEntity<BigDecimal> getTotalOutstanding(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                loanService.getTotalOutstanding(userId)
        );
    }
}
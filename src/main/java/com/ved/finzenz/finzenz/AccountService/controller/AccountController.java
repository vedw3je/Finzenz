package com.ved.finzenz.finzenz.AccountService.controller;
import com.ved.finzenz.finzenz.AccountService.dto.AccountResponse;
import com.ved.finzenz.finzenz.AccountService.entity.Account;
import com.ved.finzenz.finzenz.AccountService.request.AccountRequest;
import com.ved.finzenz.finzenz.AccountService.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Account APIs", description = "Manage user financial accounts and balances")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @Operation(
            summary = "Create a new account",
            description = "Allows ADMIN to create a new financial account for a user"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Account created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@RequestBody @Valid AccountRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(accountService.createAccount(request));
    }

    @Operation(
            summary = "Get all accounts for a user",
            description = "Returns a list of all accounts associated with a given user ID"
    )
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST','VIEWER')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AccountResponse>> getAccounts(@PathVariable Long userId) {
        return ResponseEntity.ok(accountService.getAccountsByUser(userId));
    }

    @Operation(
            summary = "Get account details",
            description = "Fetch account details by account ID"
    )
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST','VIEWER')")
    @GetMapping("/{accountId}")
    public ResponseEntity<AccountResponse> getAccount(@PathVariable Long accountId) {
        return ResponseEntity.ok(accountService.getAccountById(accountId));
    }

    @Operation(
            summary = "Get account balance",
            description = "Returns current balance of a specific account"
    )
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST','VIEWER')")
    @GetMapping("/{accountId}/balance")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable Long accountId) {
        return ResponseEntity.ok(accountService.getAccountBalance(accountId));
    }

    @Operation(
            summary = "Get user net worth",
            description = "Calculates total balance across all accounts of a user"
    )
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST','VIEWER')")
    @GetMapping("/net-worth/{userId}")
    public ResponseEntity<BigDecimal> getNetWorth(@PathVariable Long userId) {
        return ResponseEntity.ok(accountService.getNetWorth(userId));
    }

    @Operation(
            summary = "Delete an account",
            description = "Allows ADMIN to delete an account permanently"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Account deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{accountId}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long accountId) {
        accountService.deleteAccount(accountId);
        return ResponseEntity.noContent().build();
    }
}

package com.ved.finzenz.finzenz.BudgetService.controller;

import com.ved.finzenz.finzenz.BudgetService.dto.BudgetResponse;
import com.ved.finzenz.finzenz.BudgetService.entity.Budget;
import com.ved.finzenz.finzenz.BudgetService.request.BudgetRequest;
import com.ved.finzenz.finzenz.BudgetService.service.BudgetService;
import com.ved.finzenz.finzenz.BudgetService.service.BudgetServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Tag(name = "Budget APIs", description = "Manage user budgets and track spending limits")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    // ---------------- CREATE BUDGET ----------------
    @Operation(
            summary = "Create a budget",
            description = "Allows ADMIN or ANALYST to create a budget for a specific category and time range"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Budget created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
    @PostMapping
    public ResponseEntity<BudgetResponse> create(@RequestBody @Valid BudgetRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(budgetService.createBudget(request));
    }

    // ---------------- GET BUDGETS BY USER ----------------
    @Operation(
            summary = "Get budgets by user",
            description = "Returns all budgets associated with a specific user"
    )
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BudgetResponse>> getByUser(@PathVariable Integer userId) {
        return ResponseEntity.ok(budgetService.getBudgetsByUserId(userId));
    }

    // ---------------- GET BUDGETS BY CATEGORY ----------------
    @Operation(
            summary = "Get budgets by category",
            description = "Fetch budgets for a user filtered by category"
    )
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
    @GetMapping("/user/{userId}/category/{category}")
    public ResponseEntity<List<BudgetResponse>> getByCategory(
            @PathVariable Integer userId,
            @PathVariable String category) {
        return ResponseEntity.ok(budgetService.getBudgetsByCategory(userId, category));
    }

    // ---------------- GET REMAINING BUDGET ----------------
    @Operation(
            summary = "Get remaining budget",
            description = "Calculates remaining budget for a given category based on transactions"
    )
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
    @GetMapping("/remaining")
    public ResponseEntity<BigDecimal> getRemaining(
            @RequestParam Integer userId,
            @RequestParam String category,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        return ResponseEntity.ok(
                budgetService.getRemainingBudget(userId, category, date)
        );
    }

    // ---------------- UPDATE BUDGET ----------------
    @Operation(
            summary = "Update a budget",
            description = "Allows ADMIN or ANALYST to modify an existing budget"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Budget updated successfully"),
            @ApiResponse(responseCode = "404", description = "Budget not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
    @PutMapping("/{id}")
    public ResponseEntity<BudgetResponse> update(
            @PathVariable Integer id,
            @RequestBody @Valid BudgetRequest request) {

        return ResponseEntity.ok(budgetService.updateBudget(id, request));
    }

    // ---------------- DELETE BUDGET ----------------
    @Operation(
            summary = "Delete a budget",
            description = "Allows ADMIN to delete a budget permanently"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Budget deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Budget not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        budgetService.deleteBudget(id);
        return ResponseEntity.noContent().build();
    }
}
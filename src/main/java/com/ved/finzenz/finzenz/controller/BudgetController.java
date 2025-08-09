package com.ved.finzenz.finzenz.controller;

import com.ved.finzenz.finzenz.dto.BudgetResponse;
import com.ved.finzenz.finzenz.entities.Budget;
import com.ved.finzenz.finzenz.request.BudgetRequest;
import com.ved.finzenz.finzenz.service.BudgetServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/budgets")
public class BudgetController {
    private final BudgetServiceImpl budgetService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BudgetResponse>> getBudgetsByUser(@PathVariable Integer userId) {
        List<Budget> budgets = budgetService.getBudgetsByUserId(userId);
        List<BudgetResponse> responses = budgets.stream()
                .map(BudgetResponse::new)
                .toList();

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/user/{userId}/category/{category}")
    public ResponseEntity<List<BudgetResponse>> getBudgetsByCategory(
            @PathVariable Integer userId,
            @PathVariable String category
    ) {
        List<Budget> budgets = budgetService.getBudgetsByCategory(userId, category);
        List<BudgetResponse> responses = budgets.stream()
                .map(BudgetResponse::new)
                .toList();
        return ResponseEntity.ok(responses);
    }


    @GetMapping("/remaining")
    public ResponseEntity<BigDecimal> getRemainingBudget(
            @RequestParam Integer userId,
            @RequestParam String category,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        BigDecimal remainingBudget = budgetService.getRemainingBudget(userId, category, date);
        return ResponseEntity.ok(remainingBudget);
    }

    @PostMapping
    public ResponseEntity<BudgetResponse> createBudget(@Valid @RequestBody BudgetRequest budgetRequest){
        Budget budget = Budget.builder()
                .userId(budgetRequest.getUserId())
                .amount(budgetRequest.getAmount())
                .category(budgetRequest.getCategory())
                .startDate(budgetRequest.getStartDate())
                .endDate(budgetRequest.getEndDate())
                .build();

        Budget created = budgetService.createBudget(budget);
        return ResponseEntity.status(HttpStatus.CREATED).body(new BudgetResponse(created));
    }

    @PutMapping("/user/{userId}")
    public ResponseEntity<BudgetResponse> updateBudget(
            @PathVariable Integer id,
            @Valid @RequestBody BudgetRequest budgetRequest) {

        Budget budget = Budget.builder()
                .userId(budgetRequest.getUserId())
                .amount(budgetRequest.getAmount())
                .category(budgetRequest.getCategory())
                .startDate(budgetRequest.getStartDate())
                .endDate(budgetRequest.getEndDate())
                .build();

        Budget updated = budgetService.updateBudget(id, budget);
        return ResponseEntity.ok(new BudgetResponse(updated));
    }

    // DELETE
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Void> deleteBudget(@PathVariable Integer id) {
        budgetService.deleteBudget(id);
        return ResponseEntity.noContent().build();
    }
}

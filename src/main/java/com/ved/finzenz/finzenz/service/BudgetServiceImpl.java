package com.ved.finzenz.finzenz.service;

import com.ved.finzenz.finzenz.entities.Budget;
import com.ved.finzenz.finzenz.entities.Transaction;
import com.ved.finzenz.finzenz.repository.BudgetRepository;
import com.ved.finzenz.finzenz.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BudgetServiceImpl implements BudgetService{

    private final BudgetRepository budgetRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionServiceImpl transactionService;

    @Override
    public Budget createBudget(Budget budget) {
        return budgetRepository.save(budget);
    }

    @Override
    public List<Budget> getBudgetsByUserId(Integer userId) {
        return budgetRepository.findByUserId(userId);
    }

    @Override
    public List<Budget> getBudgetsByCategory(Integer userId, String category) {
        return budgetRepository.findByUserIdAndCategoryIgnoreCase(userId, category);
    }


    @Override
    public Budget updateBudget(Integer id, Budget budget) {
        Budget existing = budgetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Budget not found"));
        existing.setCategory(budget.getCategory());
        existing.setAmount(budget.getAmount());
        existing.setStartDate(budget.getStartDate());
        existing.setEndDate(budget.getEndDate());
        return budgetRepository.save(existing);
    }

    @Override
    public void deleteBudget(Integer id) {
        budgetRepository.deleteById(id);
    }

//    Needs fix

    @Override
    public BigDecimal getRemainingBudget(Integer userId, String category, LocalDate date) {
        List<Budget> budgets = budgetRepository.findByUserIdAndCategoryIgnoreCase(userId, category);

        // Filter to active budgets on the given date
        budgets = budgets.stream()
                .filter(b -> !date.isBefore(b.getStartDate()) && !date.isAfter(b.getEndDate()))
                .toList();

        if (budgets.isEmpty()) {
            throw new RuntimeException("No active budget found for category: " + category);
        }

        // Sum all matching budgets
        BigDecimal totalBudget = budgets.stream()
                .map(Budget::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // ✅ Use existing method from TransactionService
        BigDecimal totalSpent = transactionService.getTotalSpendingByCategoryForUser(userId, category);
        System.out.println("Total spent for "+category + " is " + totalSpent);

        return totalBudget.subtract(totalSpent);
    }

}

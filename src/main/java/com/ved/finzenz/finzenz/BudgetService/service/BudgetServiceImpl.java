package com.ved.finzenz.finzenz.BudgetService.service;

import com.ved.finzenz.finzenz.BudgetService.dto.BudgetResponse;
import com.ved.finzenz.finzenz.BudgetService.entity.Budget;
import com.ved.finzenz.finzenz.BudgetService.mapper.BudgetMapper;
import com.ved.finzenz.finzenz.BudgetService.repository.BudgetRepository;
import com.ved.finzenz.finzenz.BudgetService.request.BudgetRequest;
import com.ved.finzenz.finzenz.TransactionService.repository.TransactionRepository;
import com.ved.finzenz.finzenz.TransactionService.service.TransactionService;
import com.ved.finzenz.finzenz.TransactionService.service.TransactionServiceImpl;
import com.ved.finzenz.finzenz.UserService.entity.User;
import com.ved.finzenz.finzenz.UserService.repository.UserRepository;
import com.ved.finzenz.finzenz.exceptions.BudgetNotFoundException;
import com.ved.finzenz.finzenz.exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BudgetServiceImpl implements BudgetService {

    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;
    private final TransactionService transactionService;
    private final BudgetMapper budgetMapper;

    @Override
    public BudgetResponse createBudget(BudgetRequest request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Budget budget = budgetMapper.toEntity(request, user);

        return budgetMapper.toResponse(budgetRepository.save(budget));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BudgetResponse> getBudgetsByUserId(Integer userId) {
        return budgetRepository.findByUserId(userId)
                .stream()
                .map(budgetMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BudgetResponse> getBudgetsByCategory(Integer userId, String category) {
        return budgetRepository.findByUserIdAndCategoryIgnoreCase(userId, category)
                .stream()
                .map(budgetMapper::toResponse)
                .toList();
    }

    @Override
    public BudgetResponse updateBudget(Integer id, BudgetRequest request) {

        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new BudgetNotFoundException("Budget not found"));

        budget.setCategory(request.getCategory());
        budget.setAmount(request.getAmount());
        budget.setStartDate(request.getStartDate());
        budget.setEndDate(request.getEndDate());

        return budgetMapper.toResponse(budget);
    }

    @Override
    public void deleteBudget(Integer id) {
        if (!budgetRepository.existsById(id)) {
            throw new BudgetNotFoundException("Budget not found");
        }
        budgetRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getRemainingBudget(Integer userId, String category, LocalDate date) {

        BigDecimal totalBudget = budgetRepository
                .getActiveBudgetTotal(userId, category, date);

        if (totalBudget.compareTo(BigDecimal.ZERO) == 0) {
            throw new BudgetNotFoundException("No active budget found");
        }

        BigDecimal spent = transactionService
                .getTotalSpendingByCategory(userId, category);

        return totalBudget.subtract(spent);
    }
}
package com.ved.finzenz.finzenz.TransactionService.service;
import com.ved.finzenz.finzenz.AccountService.entity.Account;
import com.ved.finzenz.finzenz.AccountService.repository.AccountRepository;
import com.ved.finzenz.finzenz.AccountService.service.AccountServiceImpl;
import com.ved.finzenz.finzenz.TransactionService.dto.TransactionResponse;
import com.ved.finzenz.finzenz.TransactionService.entity.Transaction;
import com.ved.finzenz.finzenz.TransactionService.enums.TransactionType;
import com.ved.finzenz.finzenz.TransactionService.mapper.TransactionMapper;
import com.ved.finzenz.finzenz.TransactionService.repository.TransactionRepository;
import com.ved.finzenz.finzenz.AccountService.request.AccountRequest;
import com.ved.finzenz.finzenz.TransactionService.request.TransactionRequest;
import com.ved.finzenz.finzenz.exceptions.AccountNotFoundException;
import com.ved.finzenz.finzenz.exceptions.TransactionNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final TransactionMapper mapper;

    @Override
    public TransactionResponse createTransaction(TransactionRequest request) {

        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        Transaction transaction = mapper.toEntity(request, account);

        // 🔥 Correct balance logic
        if (transaction.getTransactionType() == TransactionType.DEBIT) {
            if (account.getBalance().compareTo(transaction.getAmount()) < 0) {
                throw new IllegalStateException("Insufficient balance");
            }
            account.setBalance(account.getBalance().subtract(transaction.getAmount()));
        } else {
            account.setBalance(account.getBalance().add(transaction.getAmount()));
        }

        // No explicit save needed → JPA dirty checking
        transactionRepository.save(transaction);

        return mapper.toResponse(transaction);
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionResponse getTransactionById(Integer id) {
        return transactionRepository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found"));
    }

    @Override
    public TransactionResponse updateTransaction(Integer id, TransactionRequest request) {

        Transaction existing = transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found"));

        existing.setAmount(request.getAmount());
        existing.setTransactionDate(request.getTransactionDate());
        existing.setDescription(request.getDescription());
        existing.setTransactionType(request.getTransactionType());
        existing.setCategory(request.getCategory());

        return mapper.toResponse(existing);
    }


    @Override
    public void deleteTransaction(Integer id) {
        if (!transactionRepository.existsById(id)) {
            throw new TransactionNotFoundException("Transaction not found");
        }
        transactionRepository.deleteById(id);
    }

    // ---------------- DASHBOARD READY ----------------

    @Override
    @Transactional(readOnly = true)
    public List<TransactionResponse> getTransactionsByUser(Integer userId) {
        return transactionRepository.findByAccountUserId(userId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionResponse> getTransactionsByAccountId(Long accountId) {

        return transactionRepository.findByAccountId(accountId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionResponse> getUserMonthlyTransactions(Integer userId, int month, int year) {

        return transactionRepository.findUserMonthlyTransactions(userId, month, year)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionResponse> searchTransactionsByDescriptionForUser(
            Integer userId, String keyword) {

        return transactionRepository.searchByDescription(userId, keyword)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionResponse> getTransactionsByUserIdAndDateRange(
            Integer userId,
            LocalDateTime startDate,
            LocalDateTime endDate) {

        return transactionRepository
                .findByUserIdAndDateRange(userId, startDate, endDate)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public BigDecimal getTotalSpendingByCategory(Integer userId, String category) {
        return transactionRepository.getTotalSpendingByCategory(userId, category);
    }

    @Override
    public BigDecimal getTotalIncome(Integer userId) {
        return transactionRepository.getTotalIncome(userId);
    }

    @Override
    public BigDecimal getTotalExpense(Integer userId) {
        return transactionRepository.getTotalExpense(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransactionResponse> getTransactionsPaginated(Integer userId, Pageable pageable) {

        return transactionRepository
                .findByAccountUserId(userId, pageable)
                .map(mapper::toResponse);
    }


    @Override
    public BigDecimal getNetBalance(Integer userId) {
        BigDecimal income = transactionRepository.getTotalIncome(userId);
        BigDecimal expense = transactionRepository.getTotalExpense(userId);
        return income.subtract(expense);
    }
}
package com.ved.finzenz.finzenz.service;
import com.ved.finzenz.finzenz.dto.AccountResponse;
import com.ved.finzenz.finzenz.entities.Account;
import com.ved.finzenz.finzenz.entities.Transaction;
import com.ved.finzenz.finzenz.repository.AccountRepository;
import com.ved.finzenz.finzenz.repository.TransactionRepository;
import com.ved.finzenz.finzenz.request.AccountRequest;
import com.ved.finzenz.finzenz.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountServiceImpl accountService;

    // ------------------- CRUD -------------------

    @Override
    public Transaction createTransaction(Transaction transaction) {
        Account account = accountService.getAccountbyID(transaction.getAccountId());
        account.setBalance(account.getBalance().subtract(transaction.getAmount()));

        AccountRequest request = new AccountRequest(account.getAccountName(),
                account.getAccountType(),
                account.getInstitutionName(),
                account.getAccountNumber(),
                account.getBalance(),
                account.getCurrency(),
                account.getIsActive(),
                account.getUser().getId());

        System.out.println(transaction.getAmount());
        System.out.println(account.getBalance());

        accountService.updateAccount(request, account.getAccountId());

        return transactionRepository.save(transaction);
    }

    @Override
    public Transaction getTransactionById(Integer id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + id));
    }

    @Override
    public Transaction updateTransaction(Integer id, Transaction transaction) {
        Transaction existing = getTransactionById(id);

        existing.setAccountId(transaction.getAccountId());
        existing.setAmount(transaction.getAmount());
        existing.setTransactionDate(transaction.getTransactionDate());
        existing.setDescription(transaction.getDescription());
        existing.setTransactionType(transaction.getTransactionType());
        existing.setCategory(transaction.getCategory());

        return transactionRepository.save(existing);
    }

    @Override
    public void deleteTransaction(Integer id) {
        if (!transactionRepository.existsById(id)) {
            throw new RuntimeException("Transaction not found with id: " + id);
        }
        transactionRepository.deleteById(id);
    }

    // ------------------- Finance App Specific -------------------

    @Override
    public List<Transaction> getTransactionsByAccountId(Integer accountId) {
        return transactionRepository.findByAccountId(accountId);
    }

    @Override
    public List<Transaction> getTransactionsByUserId(Integer userId) {
        return transactionRepository.findByUserId(userId); // native query
    }

    @Override
    public List<Transaction> getUserMonthlyTransactions(Integer userId, int month, int year) {
        return transactionRepository.findUserMonthlyTransactions(userId, month, year);
    }



    @Override
    public List<Transaction> getTransactionsByUserIdAndDateRange(Integer userId, LocalDateTime startDate, LocalDateTime endDate) {
        // Could also use a native query, but here we fetch first then filter
        return getTransactionsByUserId(userId).stream()
                .filter(t -> !t.getTransactionDate().isBefore(startDate) && !t.getTransactionDate().isAfter(endDate))
                .toList();
    }



    @Override
    public BigDecimal getTotalSpendingByCategoryForUser(Integer userId, String category) {
        return getTransactionsByUserId(userId).stream()
                .filter(t -> category.equalsIgnoreCase(t.getCategory()) && t.getTransactionType() == Transaction.TransactionType.DEBIT)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }



    @Override
    public BigDecimal getTotalIncomeForUser(Integer userId) {
        return getTransactionsByUserId(userId).stream()
                .filter(t -> t.getTransactionType() == Transaction.TransactionType.CREDIT)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }



    @Override
    public BigDecimal getTotalExpenseForUser(Integer userId) {
        return getTransactionsByUserId(userId).stream()
                .filter(t -> t.getTransactionType() == Transaction.TransactionType.DEBIT)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }



    @Override
    public List<Transaction> searchTransactionsByDescriptionForUser(Integer userId, String keyword) {
        return getTransactionsByUserId(userId).stream()
                .filter(t -> t.getDescription() != null && t.getDescription().toLowerCase().contains(keyword.toLowerCase()))
                .toList();
    }
}
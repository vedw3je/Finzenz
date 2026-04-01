package com.ved.finzenz.finzenz.LoanService.service;

import com.ved.finzenz.finzenz.LoanService.dto.LoanResponse;
import com.ved.finzenz.finzenz.LoanService.dto.LoanSummaryDto;
import com.ved.finzenz.finzenz.LoanService.dto.UpcomingEmiDto;
import com.ved.finzenz.finzenz.AccountService.entity.Account;
import com.ved.finzenz.finzenz.LoanService.entity.Loan;
import com.ved.finzenz.finzenz.LoanService.mapper.LoanMapper;
import com.ved.finzenz.finzenz.LoanService.request.LoanRequest;
import com.ved.finzenz.finzenz.TransactionService.entity.Transaction;
import com.ved.finzenz.finzenz.AccountService.repository.AccountRepository;
import com.ved.finzenz.finzenz.LoanService.repository.LoanRepository;
import com.ved.finzenz.finzenz.TransactionService.enums.TransactionType;
import com.ved.finzenz.finzenz.TransactionService.repository.TransactionRepository;
import com.ved.finzenz.finzenz.exceptions.AccountNotFoundException;
import com.ved.finzenz.finzenz.exceptions.LoanNotFoundException;
import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
@Transactional
public class LoanServiceImpl implements LoanService {

    private final LoanRepository loanRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final LoanMapper mapper;

    @Override
    public LoanSummaryDto createLoan(LoanRequest request) {

        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        Loan loan = mapper.toEntity(request, account);

        // 🔥 Business logic (cleanly structured)
        initializeLoan(loan);

        Loan saved = loanRepository.save(loan);

        // 🔥 Disbursement transaction
        createDisbursementTransaction(saved, account);

        return mapper.toSummary(saved, calculateOutstanding(saved));
    }

    private void initializeLoan(Loan loan) {

        if (loan.getRecurringIntervalDays() <= 0) {
            throw new IllegalArgumentException("Invalid recurring interval");
        }

        if (loan.getStartDate() == null) {
            loan.setStartDate(LocalDate.now());
        }

        if (loan.getEndDate() == null) {
            throw new IllegalArgumentException("End date required");
        }

        if (loan.getTotalInstallments() == null) {
            long days = ChronoUnit.DAYS.between(
                    loan.getStartDate(),
                    loan.getEndDate()
            );
            loan.setTotalInstallments(
                    (int) Math.ceil((double) days / loan.getRecurringIntervalDays())
            );
        }

        if (loan.getEmiAmount() == null) {
            loan.setEmiAmount(
                    calculateEmi(loan)
            );
        }

        loan.setNextDueDate(
                loan.getStartDate().plusDays(loan.getRecurringIntervalDays())
        );

        loan.setStatus(Loan.LoanStatus.ACTIVE);
    }

    private BigDecimal calculateEmi(Loan loan) {
        return loan.getPrincipalAmount()
                .divide(BigDecimal.valueOf(loan.getTotalInstallments()), 2, RoundingMode.HALF_UP);
    }

    private void createDisbursementTransaction(Loan loan, Account account) {

        Transaction txn = Transaction.builder()
                .account(account)
                .amount(loan.getPrincipalAmount())
                .transactionType(TransactionType.CREDIT)
                .transactionDate(LocalDateTime.now())
                .category("Loan")
                .description("Loan disbursement")
                .build();

        account.setBalance(account.getBalance().add(loan.getPrincipalAmount()));

        transactionRepository.save(txn);
    }

    @Override
    public List<LoanSummaryDto> getLoanSummary(Long accountId) {
        return loanRepository.findByAccountId(accountId)
                .stream()
                .map(l -> mapper.toSummary(l, calculateOutstanding(l)))
                .toList();
    }

    @Override
    public List<LoanSummaryDto> getLoanSummaryByUser(Long userId) {
        return loanRepository.findByAccountUserId(userId)
                .stream()
                .map(l -> mapper.toSummary(l, calculateOutstanding(l)))
                .toList();
    }

    @Override
    public void recordEmiPayment(Long loanId) {

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new LoanNotFoundException("Loan not found"));

        Account account = loan.getAccount();

        if (loan.isCompleted()) {
            throw new IllegalStateException("Loan already completed");
        }

        if (account.getBalance().compareTo(loan.getEmiAmount()) < 0) {
            throw new IllegalStateException("Insufficient balance");
        }

        account.setBalance(account.getBalance().subtract(loan.getEmiAmount()));

        Transaction txn = Transaction.builder()
                .account(account)
                .amount(loan.getEmiAmount())
                .transactionType(TransactionType.DEBIT)
                .transactionDate(LocalDateTime.now())
                .category("Loan EMI")
                .description("EMI payment")
                .build();

        transactionRepository.save(txn);

        loan.setCompletedInstallments(loan.getCompletedInstallments() + 1);
        loan.setLastPaymentDate(LocalDate.now());

        if (loan.isCompleted()) {
            loan.setStatus(Loan.LoanStatus.CLOSED);
            loan.setNextDueDate(null);
        } else {
            loan.setNextDueDate(LocalDate.now().plusDays(loan.getRecurringIntervalDays()));
        }
    }

    public BigDecimal calculateOutstanding(Loan loan) {
        return loan.getEmiAmount()
                .multiply(BigDecimal.valueOf(loan.getRemainingInstallments()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UpcomingEmiDto> getUpcomingEmis(Long userId) {

        LocalDate today = LocalDate.now();
        LocalDate next7Days = today.plusDays(7);

        return loanRepository.findByAccountUserId(userId)
                .stream()
                .filter(loan -> loan.getStatus() == Loan.LoanStatus.ACTIVE)
                .filter(loan -> loan.getNextDueDate() != null &&
                        !loan.getNextDueDate().isBefore(today) &&
                        !loan.getNextDueDate().isAfter(next7Days))
                .map(mapper::toUpcomingEmi)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoanResponse> getOverdueLoans(Long userId) {

        return loanRepository.findByAccountUserId(userId)
                .stream()
                .filter(Loan::isOverdue)
                .map(loan -> mapper.toResponse(
                        loan,
                        calculateOutstanding(loan)
                ))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalOutstanding(Long userId) {

        return loanRepository.findByAccountUserId(userId)
                .stream()
                .filter(loan -> loan.getStatus() == Loan.LoanStatus.ACTIVE)
                .map(this::calculateOutstanding)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    @Transactional(readOnly = true)
    public Loan getLoanById(Long loanId) {

        return loanRepository.findById(loanId)
                .orElseThrow(() -> new LoanNotFoundException("Loan not found"));
    }
}
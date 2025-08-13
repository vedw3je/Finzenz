package com.ved.finzenz.finzenz.service;

import com.ved.finzenz.finzenz.dto.LoanSummaryDto;
import com.ved.finzenz.finzenz.dto.UpcomingEmiDto;
import com.ved.finzenz.finzenz.entities.Account;
import com.ved.finzenz.finzenz.entities.Loan;
import com.ved.finzenz.finzenz.entities.Transaction;
import com.ved.finzenz.finzenz.repository.AccountRepository;
import com.ved.finzenz.finzenz.repository.LoanRepository;
import com.ved.finzenz.finzenz.repository.TransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LoanServiceImpl implements LoanService{

    private final LoanRepository loanRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public LoanServiceImpl(LoanRepository loanRepository, AccountRepository accountRepository, TransactionRepository transactionRepository){
        this.accountRepository = accountRepository;
        this.loanRepository = loanRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    @Transactional
    public Loan createLoan(Loan loan) {
        // Validate account exists
        Account account = accountRepository.findById(loan.getAccountId())
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));

        // Validate required fields for all recurring loans
        if (loan.getRecurringIntervalDays() == null || loan.getRecurringIntervalDays() <= 0) {
            throw new IllegalArgumentException("Recurring interval days must be specified and positive");
        }

        // Calculate and set missing fields if not provided
        if (loan.getTotalInstallments() == null) {
            if (loan.getStartDate() == null || loan.getEndDate() == null) {
                throw new IllegalArgumentException("Either totalInstallments or both startDate and endDate must be provided");
            }
            long totalDays = loan.getStartDate().until(loan.getEndDate()).getDays();
            loan.setTotalInstallments((int) Math.ceil((double) totalDays / loan.getRecurringIntervalDays()));
        }

        // Set default dates if not provided
        if (loan.getStartDate() == null) {
            loan.setStartDate(LocalDate.now());
        }

        if (loan.getEndDate() == null) {
            if (loan.getTotalInstallments() != null && loan.getRecurringIntervalDays() != null) {
                loan.setEndDate(loan.getStartDate().plusDays(
                        (long) loan.getTotalInstallments() * loan.getRecurringIntervalDays()));
            } else {
                throw new IllegalArgumentException("Cannot calculate end date without totalInstallments and recurringIntervalDays");
            }
        }

        // Calculate EMI if not provided
        if (loan.getEmiAmount() == null) {
            loan.setEmiAmount(calculateRecurringEmi(
                    loan.getPrincipalAmount(),
                    loan.getInterestRate(),
                    loan.getTotalInstallments(),
                    loan.getRecurringIntervalDays()
            ));
        }

        // Set other default values
        if (loan.getCompletedInstallments() == null) {
            loan.setCompletedInstallments(0);
        }

        if (loan.getNextDueDate() == null) {
            loan.setNextDueDate(loan.getStartDate().plusDays(loan.getRecurringIntervalDays()));
        }

        if (loan.getStatus() == null) {
            loan.setStatus(Loan.LoanStatus.ACTIVE);
        }

        // Calculate tenure in months for reference
        if (loan.getTenureMonths() == null) {
            loan.setTenureMonths((int) Math.ceil(loan.getStartDate().until(loan.getEndDate()).toTotalMonths()));
        }

        Loan savedLoan = loanRepository.save(loan);

        // Create loan disbursement transaction
        Transaction disbursement = Transaction.builder()
                .accountId(account.getAccountId())
                .amount(loan.getPrincipalAmount())
                .transactionType(Transaction.TransactionType.CREDIT)
                .transactionDate(LocalDateTime.now())
                .category("Loan Disbursement")
                .description("Loan disbursement from " + loan.getLenderName() +
                        " (" + getPaymentFrequencyDescription(loan.getRecurringIntervalDays()) + " payments)")
                .build();
        transactionRepository.save(disbursement);

        // Update account balance
        account.setBalance(account.getBalance().add(loan.getPrincipalAmount()));
        accountRepository.save(account);

        return savedLoan;
    }

    private BigDecimal calculateRecurringEmi(BigDecimal principal, BigDecimal annualRate,
                                             int totalInstallments, int intervalDays) {
        if (annualRate.compareTo(BigDecimal.ZERO) == 0) {
            // Zero interest - simple division
            return principal.divide(BigDecimal.valueOf(totalInstallments), 2, RoundingMode.HALF_UP);
        }

        // Calculate period interest rate
        double periodsPerYear = 365.0 / intervalDays;
        BigDecimal periodRate = annualRate
                .divide(BigDecimal.valueOf(periodsPerYear * 100), 10, RoundingMode.HALF_UP);

        // EMI calculation: P * r * (1+r)^n / ((1+r)^n - 1)
        BigDecimal onePlusR = periodRate.add(BigDecimal.ONE);
        BigDecimal onePlusRPowerN = onePlusR.pow(totalInstallments);

        return principal
                .multiply(periodRate)
                .multiply(onePlusRPowerN)
                .divide(onePlusRPowerN.subtract(BigDecimal.ONE), 2, RoundingMode.HALF_UP);
    }

    @Override
    public List<LoanSummaryDto> getLoanSummary(Long accountId) {
        return loanRepository.findByAccountId(accountId).stream()
                .map(loan -> LoanSummaryDto.builder()
                        .loanId(loan.getId())
                        .lender(loan.getLenderName())
                        .emiAmount(loan.getEmiAmount())
                        .nextDueDate(loan.getNextDueDate())
                        .status(loan.getStatus())
                        .outstandingAmount(calculateOutstanding(loan))
                        .build()
                ).collect(Collectors.toList());
    }

    @Override
    public List<UpcomingEmiDto> getUpcomingEmis(Long accountId) {
        LocalDate today = LocalDate.now();
        LocalDate upcomingLimit = today.plusDays(7);

        return loanRepository.findByAccountId(accountId).stream()
                .filter(loan -> loan.getStatus() == Loan.LoanStatus.ACTIVE)
                .filter(loan -> loan.getNextDueDate() != null &&
                        !loan.getNextDueDate().isBefore(today) &&
                        loan.getNextDueDate().isBefore(upcomingLimit))
                .map(loan -> UpcomingEmiDto.builder()
                        .loanId(loan.getId())
                        .lender(loan.getLenderName())
                        .amount(loan.getEmiAmount())
                        .dueDate(loan.getNextDueDate())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void recordEmiPayment(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new EntityNotFoundException("Loan not found"));

        Account account = accountRepository.findById(loan.getAccountId())
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));

        if (loan.getStatus() != Loan.LoanStatus.ACTIVE) {
            throw new IllegalStateException("Cannot make payment on inactive loan");
        }

        if (loan.isCompleted()) {
            throw new IllegalStateException("Loan is already fully paid");
        }

        if (account.getBalance().compareTo(loan.getEmiAmount()) < 0) {
            throw new IllegalStateException("Insufficient balance");
        }

        // Deduct balance
        account.setBalance(account.getBalance().subtract(loan.getEmiAmount()));
        accountRepository.save(account);

        // Create EMI transaction
        Transaction emiTransaction = Transaction.builder()
                .accountId(account.getAccountId())
                .amount(loan.getEmiAmount())
                .transactionType(Transaction.TransactionType.DEBIT)
                .transactionDate(LocalDateTime.now())
                .category("Loan EMI")
                .description("EMI payment to " + loan.getLenderName() +
                        " (Payment " + (loan.getCompletedInstallments() + 1) +
                        " of " + loan.getTotalInstallments() + ")")
                .build();
        transactionRepository.save(emiTransaction);

        // Update loan payment tracking
        loan.setLastPaymentDate(LocalDate.now());
        loan.setCompletedInstallments(loan.getCompletedInstallments() + 1);

        // Check if loan is completed
        if (loan.isCompleted()) {
            loan.setStatus(Loan.LoanStatus.CLOSED);
            loan.setNextDueDate(null);
        } else {
            // Calculate next due date
            loan.setNextDueDate(LocalDate.now().plusDays(loan.getRecurringIntervalDays()));
        }

        loanRepository.save(loan);
    }

    public BigDecimal calculateOutstanding(Loan loan) {
        if (loan.getStatus() == Loan.LoanStatus.CLOSED || loan.isCompleted()) {
            return BigDecimal.ZERO;
        }

        int remainingInstallments = loan.getRemainingInstallments();
        return loan.getEmiAmount().multiply(BigDecimal.valueOf(remainingInstallments));
    }

    private String getPaymentFrequencyDescription(Integer intervalDays) {
        if (intervalDays == null) return "Unknown";

        return switch (intervalDays) {
            case 1 -> "Daily";
            case 7 -> "Weekly";
            case 14 -> "Bi-weekly";
            case 15 -> "Semi-monthly";
            case 30 -> "Monthly";
            case 90 -> "Quarterly";
            default -> "Every " + intervalDays + " days";
        };
    }

    // Additional utility methods for loan management
    public List<Loan> getOverdueLoans(Long accountId) {
        return loanRepository.findByAccountId(accountId).stream()
                .filter(Loan::isOverdue)
                .collect(Collectors.toList());
    }

    public BigDecimal getTotalOutstanding(Long accountId) {
        return loanRepository.findByAccountId(accountId).stream()
                .filter(loan -> loan.getStatus() == Loan.LoanStatus.ACTIVE)
                .map(this::calculateOutstanding)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public Loan getLoanById(Long loanId) {
        return null;
    }
}
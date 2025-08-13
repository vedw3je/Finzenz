package com.ved.finzenz.finzenz.scheduler;

import com.ved.finzenz.finzenz.entities.Loan;
import com.ved.finzenz.finzenz.entities.Loan.LoanStatus;
import com.ved.finzenz.finzenz.entities.Transaction;
import com.ved.finzenz.finzenz.repository.LoanRepository;
import com.ved.finzenz.finzenz.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecurringLoanScheduler {

    private final LoanRepository loanRepository;
    private final TransactionRepository transactionRepository;

    /**
     * Runs every day at midnight
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void processRecurringLoanPayments() {
        log.info("Starting recurring loan payment job...");

        List<Loan> dueLoans = loanRepository.findByStatusAndNextDueDateLessThanEqual(
                LoanStatus.ACTIVE,
                LocalDate.now()
        );

        for (Loan loan : dueLoans) {
            try {
                processLoanPayment(loan);
            } catch (Exception e) {
                log.error("Failed to process loan ID {}: {}", loan.getId(), e.getMessage(), e);
            }
        }

        log.info("Recurring loan payment job completed.");
    }

    private void processLoanPayment(Loan loan) {
        BigDecimal emiAmount = loan.getEmiAmount();

        // Create a transaction
        Transaction transaction = Transaction.builder()
                .accountId(loan.getAccountId())
                .amount(emiAmount)
                .transactionType(Transaction.TransactionType.DEBIT) // Assuming debit from borrower
                .description("Recurring EMI payment for Loan ID " + loan.getId())
                .transactionDate(LocalDate.now().atStartOfDay())
                .build();

        transactionRepository.save(transaction);

        // Update loan
        loan.setLastPaymentDate(LocalDate.now());
        loan.setCompletedInstallments(loan.getCompletedInstallments() + 1);
        loan.setNextDueDate(loan.calculateNextDueDate());

        if (loan.isCompleted()) {
            loan.setStatus(LoanStatus.CLOSED);
        }

        loanRepository.save(loan);

        log.info("Processed EMI payment for Loan ID {}", loan.getId());
    }
}

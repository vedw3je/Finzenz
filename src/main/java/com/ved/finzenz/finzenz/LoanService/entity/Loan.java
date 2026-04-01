package com.ved.finzenz.finzenz.LoanService.entity;

import com.ved.finzenz.finzenz.AccountService.entity.Account;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "loans")
public class Loan implements Serializable {

    public enum LoanStatus {
        ACTIVE,
        CLOSED,
        DEFAULTED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;


    @Column(name = "lender_name", nullable = false, length = 150)
    private String lenderName;


    @Column(name = "principal_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal principalAmount;


    @Column(name = "interest_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal interestRate;

    // Made nullable since we'll calculate from term in days and recurring interval
    @Column(name = "tenure_months")
    private Integer tenureMonths; // Total number of months (calculated field)


    @Column(name = "emi_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal emiAmount; // Recurring installment amount


    private LocalDate startDate; // When loan starts


    private LocalDate endDate; // Loan maturity date

    @Column(name = "next_due_date")
    private LocalDate nextDueDate; // Next installment date

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private LoanStatus status;

    // All loans are recurring - removed isRecurring field and made interval mandatory

    @Column(name = "recurring_interval_days", nullable = false)
    private Integer recurringIntervalDays; // e.g., 1=daily, 7=weekly, 15=bi-weekly, 30=monthly

    @Column(name = "last_payment_date")
    private LocalDate lastPaymentDate;

    // Additional field to track total number of installments

    @Column(name = "total_installments", nullable = false)
    private Integer totalInstallments; // Total number of payments

    // Track completed payments
    @Column(name = "completed_installments", nullable = false)
    @Builder.Default
    private Integer completedInstallments = 0; // Number of payments made

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDate createdAt;

    @PrePersist
    public void prePersist() {
        if (status == null) {
            status = LoanStatus.ACTIVE;
        }
        if (completedInstallments == null) {
            completedInstallments = 0;
        }
        // Calculate tenure in months if not set
        if (tenureMonths == null && startDate != null && endDate != null) {
            tenureMonths = (int) Math.ceil(startDate.until(endDate).toTotalMonths());
        }
    }

    // Utility methods
    public boolean isRecurring() {
        return true; // All loans are now recurring
    }

    public int getRemainingInstallments() {
        return Math.max(0, totalInstallments - completedInstallments);
    }

    public boolean isCompleted() {
        return completedInstallments >= totalInstallments;
    }

    public double getCompletionPercentage() {
        if (totalInstallments == 0) return 0.0;
        return (double) completedInstallments / totalInstallments * 100;
    }

    // Get next payment date based on recurring interval
    public LocalDate calculateNextDueDate() {
        if (lastPaymentDate != null) {
            return lastPaymentDate.plusDays(recurringIntervalDays);
        }
        return startDate.plusDays(recurringIntervalDays);
    }

    // Check if loan is overdue
    public boolean isOverdue() {
        return nextDueDate != null &&
                nextDueDate.isBefore(LocalDate.now()) &&
                status == LoanStatus.ACTIVE;
    }
}
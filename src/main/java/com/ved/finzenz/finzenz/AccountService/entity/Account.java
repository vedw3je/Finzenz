package com.ved.finzenz.finzenz.AccountService.entity;

import com.ved.finzenz.finzenz.AccountService.enums.AccountType;
import com.ved.finzenz.finzenz.AccountService.enums.CurrencyType;
import com.ved.finzenz.finzenz.UserService.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long accountId;

    private String accountName; // User-defined name for this account (e.g., "My Savings", "HDFC Bank").

    @Enumerated(EnumType.STRING)
    private AccountType accountType; // Type of account (e.g., savings, checking, credit).

    private String institutionName; // Bank or financial institution name.

    @Column(unique = true)
    private String accountNumber; // Unique account number to prevent duplicates.

    private BigDecimal balance; // Current balance in the account.

    @Enumerated(EnumType.STRING)
    private CurrencyType currency; // Currency type (e.g., INR, USD).

    private Boolean isActive;

    private LocalDateTime createdAt; // Timestamp when account was created.

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Owner of the account — many accounts can belong to one user.

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now(); // Auto-set creation time.
        if (isActive == null) isActive = true; // Default to active if not set.
    }
}
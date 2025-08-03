package com.ved.finzenz.finzenz.entities;

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
    private Long id; // Primary key for the account.

    private String accountName; // User-defined name for this account (e.g., "My Savings", "HDFC Bank").

    private String accountType; // Type of account (e.g., savings, checking, credit).

    private String institutionName; // Bank or financial institution name.

    @Column(unique = true)
    private String accountNumber; // Unique account number to prevent duplicates.

    private BigDecimal balance; // Current balance in the account.

    private String currency; // Currency type (e.g., INR, USD).

    private Boolean isActive; // Used to soft-disable an account (instead of deleting).

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


package com.ved.finzenz.finzenz.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
@Getter
@Setter
public class AccountResponse {
    private Long id;
    private String accountName;
    private String accountType;
    private String institutionName;
    private String accountNumber;
    private BigDecimal balance;
    private String currency;
    private Boolean isActive;
    private Long userId;
    private String userEmail;

    public AccountResponse() {
    }

    public AccountResponse(Long id, String accountName, String accountType, String institutionName,
                           String accountNumber, BigDecimal balance, String currency, Boolean isActive,
                           Long userId, String userEmail) {
        this.id = id;
        this.accountName = accountName;
        this.accountType = accountType;
        this.institutionName = institutionName;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.currency = currency;
        this.isActive = isActive;
        this.userId = userId;
        this.userEmail = userEmail;
    }
}


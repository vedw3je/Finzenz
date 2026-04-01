package com.ved.finzenz.finzenz.AccountService.request;

import com.ved.finzenz.finzenz.AccountService.entity.Account;
import com.ved.finzenz.finzenz.AccountService.enums.AccountType;
import com.ved.finzenz.finzenz.AccountService.enums.CurrencyType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
@Getter
@Setter
@AllArgsConstructor
public class AccountRequest {
    private String accountName;
    private AccountType accountType;
    private String institutionName;
    private String accountNumber;
    private BigDecimal balance;
    private CurrencyType currency;
    private Boolean isActive;
    private Long userId;
}
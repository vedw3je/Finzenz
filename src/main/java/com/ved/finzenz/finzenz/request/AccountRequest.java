package com.ved.finzenz.finzenz.request;

import com.ved.finzenz.finzenz.entities.Account;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
@Getter
@Setter
public class AccountRequest {
    private String accountName;
    private Account.AccountType accountType;
    private String institutionName;
    private String accountNumber;
    private BigDecimal balance;
    private Account.CurrencyType currency;
    private Boolean isActive;
    private Long userId;
}

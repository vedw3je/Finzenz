package com.ved.finzenz.finzenz.service;

import com.ved.finzenz.finzenz.dto.AccountResponse;
import com.ved.finzenz.finzenz.entities.Account;
import com.ved.finzenz.finzenz.entities.User;
import com.ved.finzenz.finzenz.exceptions.AccountNotFoundException;
import com.ved.finzenz.finzenz.exceptions.UserNotFoundException;
import com.ved.finzenz.finzenz.repository.AccountRepository;
import com.ved.finzenz.finzenz.repository.UserRepository;
import com.ved.finzenz.finzenz.request.AccountRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public AccountServiceImpl(AccountRepository accountRepository, UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    @Override
    public AccountResponse createAccount(AccountRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User with ID " + request.getUserId() + " not found"));

        Account account = new Account();
        account.setAccountName(request.getAccountName());
        account.setAccountType(request.getAccountType());
        account.setInstitutionName(request.getInstitutionName());
        account.setAccountNumber(request.getAccountNumber());
        account.setBalance(request.getBalance());
        account.setCurrency(request.getCurrency());
        account.setIsActive(request.getIsActive());
        account.setUser(user);

        Account saved = accountRepository.save(account);
        return toResponse(saved);
    }

    private AccountResponse toResponse(Account account) {
        AccountResponse response = new AccountResponse();
        response.setId(account.getAccountId());
        response.setAccountName(account.getAccountName());
        response.setAccountType(account.getAccountType());
        response.setInstitutionName(account.getInstitutionName());
        response.setAccountNumber(account.getAccountNumber());
        response.setBalance(account.getBalance());
        response.setCurrency(account.getCurrency());
        response.setIsActive(account.getIsActive());
        response.setUserId(account.getUser().getId());
        response.setUserEmail(account.getUser().getEmail());
        return response;
    }


    @Override
    public Account getAccountbyID(Long accountId) {
        if(!accountRepository.existsById(accountId)) {
            throw new AccountNotFoundException("Account with id: " + accountId + "not found");
        }
        return accountRepository.findByAccountId(accountId);
    }

    @Override
    public int getAccountBalance(Long accountId) {
        if(!accountRepository.existsById(accountId)) {
            throw new AccountNotFoundException("Account with id: " + accountId + "not found");
        }
        return accountRepository.findBalanceByAccountId(accountId);
    }

    @Override
    public List<Account> getAllAccountsforUser(Long userID) {
        return accountRepository.findByUserId(userID);
    }

    @Override
    public BigDecimal getNetWorth(Long UserID) {
        return accountRepository.getTotalBalanceByUserId(UserID);
    }

    @Override
    public boolean deleteAccount(Long accountId) {
        if(!accountRepository.existsById(accountId)) {
            throw new AccountNotFoundException("Account with id: " + accountId + "not found");
        }
        return accountRepository.deleteAccountByAccountId(accountId);
    }
}

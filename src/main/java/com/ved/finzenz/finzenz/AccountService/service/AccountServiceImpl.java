package com.ved.finzenz.finzenz.AccountService.service;

import com.ved.finzenz.finzenz.AccountService.dto.AccountResponse;
import com.ved.finzenz.finzenz.AccountService.entity.Account;
import com.ved.finzenz.finzenz.AccountService.mapper.AccountMapper;
import com.ved.finzenz.finzenz.UserService.entity.User;
import com.ved.finzenz.finzenz.exceptions.AccountNotFoundException;
import com.ved.finzenz.finzenz.exceptions.UserNotFoundException;
import com.ved.finzenz.finzenz.AccountService.repository.AccountRepository;
import com.ved.finzenz.finzenz.UserService.repository.UserRepository;
import com.ved.finzenz.finzenz.AccountService.request.AccountRequest;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final AccountMapper accountMapper;

    @Override
    public AccountResponse createAccount(AccountRequest request) {

        if (accountRepository.findByAccountNumber(request.getAccountNumber()).isPresent()) {
            throw new IllegalArgumentException("Account number already exists");
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Account account = accountMapper.toEntity(request, user);

        return accountMapper.toResponse(accountRepository.save(account));
    }

    @Override
    public AccountResponse updateAccount(AccountRequest request, Long accountId) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        if (!account.getIsActive()) {
            throw new IllegalStateException("Cannot update inactive account");
        }

        account.setBalance(request.getBalance());
        account.setCurrency(request.getCurrency());
        account.setInstitutionName(request.getInstitutionName());

        return accountMapper.toResponse(account);
    }

    @Override
    public AccountResponse getAccountById(Long accountId) {
        return accountRepository.findById(accountId)
                .map(accountMapper::toResponse)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));
    }

    @Override
    public List<AccountResponse> getAccountsByUser(Long userId) {
        return accountRepository.findByUserId(userId)
                .stream()
                .map(accountMapper::toResponse)
                .toList();
    }

    @Override
    public BigDecimal getAccountBalance(Long accountId) {
        return accountRepository.findById(accountId)
                .map(Account::getBalance)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));
    }

    @Override
    public BigDecimal getNetWorth(Long userId) {
        return Optional.ofNullable(accountRepository.getTotalBalanceByUserId(userId))
                .orElse(BigDecimal.ZERO);
    }

    @Override
    public void deleteAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        account.setIsActive(false); // ✅ soft delete
    }
}
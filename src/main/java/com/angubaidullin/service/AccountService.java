package com.angubaidullin.service;

import com.angubaidullin.dto.AccountRequestDTO;
import com.angubaidullin.dto.AccountResponseDTO;
import com.angubaidullin.entity.Account;
import com.angubaidullin.exception.AccountNotFoundException;
import com.angubaidullin.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
public class AccountService {
    private final AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;

    }

    @Transactional
    public AccountResponseDTO createAccount(AccountRequestDTO accountRequestDTO) {
        Account account = new Account(
                accountRequestDTO.getName(),
                accountRequestDTO.getEmail()
                );
        account.setBills(accountRequestDTO.getBills());

        Account savedAccount = accountRepository.save(account);
        return new AccountResponseDTO(savedAccount);
    }

    public AccountResponseDTO findAccountById(Long id) {
        Optional<Account> optionalAccount = accountRepository.findById(id);
        AccountResponseDTO accountResponseDTO = null;

        Account account = optionalAccount.orElseThrow(() -> new AccountNotFoundException("account with id: " + id + "is not found"));
            accountResponseDTO = new AccountResponseDTO(
                    account.getId(),
                    account.getName(),
                    account.getEmail(),
                    account.getBills()
            );

        return accountResponseDTO;
    }

    public List<AccountResponseDTO> findAllAccounts() {
        List<Account> accounts = accountRepository.findAll();
        return accounts.stream()
                .map(account -> new AccountResponseDTO(
                        account.getId(),
                        account.getName(),
                        account.getEmail(),
                        account.getBills()
                ))
                .toList();
    }

    @Transactional
    public AccountResponseDTO deleteAccount(Long id) {
        AccountResponseDTO accountById = findAccountById(id);
        accountRepository.deleteById(id);
        return accountById;
    }

    public List<AccountResponseDTO> findAllAccountsByName(String name) {
       return accountRepository.findByUsername(name).stream().map(account -> new AccountResponseDTO(account))
                .toList();
    }

}

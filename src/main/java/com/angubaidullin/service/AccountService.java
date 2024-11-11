package com.angubaidullin.service;

import com.angubaidullin.dto.*;
import com.angubaidullin.entity.Account;
import com.angubaidullin.exception.AccountNotFoundException;
import com.angubaidullin.repository.AccountRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    //Регистрация нового пользователя
    public AccountResponseAdminDTO register(AccountRegistrationDTO accountRegistrationDTO) {
        Account account = new Account();

        account.setName(accountRegistrationDTO.getName());
        account.setEmail(accountRegistrationDTO.getEmail());
        account.addRole("ROLE_USER");

        account.setPassword(passwordEncoder.encode(accountRegistrationDTO.getPassword()));

        accountRepository.save(account);
        return new AccountResponseAdminDTO(account);
    }

    //Добавление нового пользователя (Админ)
    @Transactional
    public AccountResponseAdminDTO createAccount(AccountCreateDTO accountCreateDTO) {
        Account account = new Account(
                accountCreateDTO.getName(),
                accountCreateDTO.getEmail()
        );
        account.setBills(accountCreateDTO.getBills());
        account.setPassword(passwordEncoder.encode(accountCreateDTO.getPassword()));

        if (accountCreateDTO.getRoles() !=null && accountCreateDTO.getRoles().size()>0) {
            account.setRoles(accountCreateDTO.getRoles());
        }

        Account savedAccount = accountRepository.save(account);
        return new AccountResponseAdminDTO(savedAccount);
    }

    //Поиск пользователя по id (Админ)
    public AccountResponseAdminDTO findAccountById(Long id) {
        Optional<Account> optionalAccount = accountRepository.findById(id);

        AccountResponseAdminDTO accountResponseAdminDTO = null;

        Account account = optionalAccount.orElseThrow(() -> new AccountNotFoundException("account with id: " + id + "is not found"));

        accountResponseAdminDTO = new AccountResponseAdminDTO(account);

        return accountResponseAdminDTO;
    }


    //Поиск всех пользователей (Админ)
    public List<AccountResponseAdminDTO> findAllAccounts() {
        List<Account> accounts = accountRepository.findAll();
        return accounts.stream()
                .map(account -> new AccountResponseAdminDTO(account))
                .toList();
    }

    //Получение данных авторизированного пользователя (Юзер)
    public AccountResponseAdminDTO getCurrentAccount(String email) {
        Optional<Account> optionalAccount = accountRepository.findByEmail(email);
        Account account = optionalAccount.orElseThrow(() -> new AccountNotFoundException("account with email: " + email + "is not found"));
        return new AccountResponseAdminDTO(account);

    }


    //Удаление пользователя (Админ)
    @Transactional
    public AccountResponseAdminDTO deleteAccount(Long id) {
        AccountResponseAdminDTO accountById = findAccountById(id);
        accountRepository.deleteById(id);
        return accountById;
    }

    //Обновление ролей пользователя (Админ)
    @Transactional
    public AccountResponseAdminDTO updateRoles(Long id, RoleUpdateDTO roleUpdateDTO) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("account with id: " + id + "is not found"));

        if (roleUpdateDTO.getRolesToRemove() != null) {
            roleUpdateDTO.getRolesToRemove().forEach(account::removeRole);
        }

        if (roleUpdateDTO.getRolesToAdd() != null) {
            roleUpdateDTO.getRolesToAdd().forEach(account::addRole);
        }

        accountRepository.save(account);
        return new AccountResponseAdminDTO(account);
    }

    //Обновление данных текущего авторизированного пользователя
    @Transactional
    public AccountResponseUserDTO updateAccount(String email, AccountUpdateDTO accountUpdateDTO) {
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new AccountNotFoundException("account with id: " + email + "is not found"));

        if (accountUpdateDTO.getName() != null) {
            account.setName(accountUpdateDTO.getName());
        }
        if (accountUpdateDTO.getEmail() != null) {
            account.setEmail(accountUpdateDTO.getEmail());
        }
        if (accountUpdateDTO.getPassword() != null) {
            account.setPassword(passwordEncoder.encode(accountUpdateDTO.getPassword()));
        }
        accountRepository.save(account);
        return new AccountResponseUserDTO(account);
    }

    //Добавление счетов для пользователя
    @Transactional
    public AccountResponseUserDTO addBills(String email, BillAddDTO billAddDTO) {
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(()-> new AccountNotFoundException("account with id: " + email + "is not found"));
        billAddDTO.getBills().forEach(account::addBill);
        accountRepository.save(account);
        return new AccountResponseUserDTO(account);
    }


}

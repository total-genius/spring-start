package com.angubaidullin.service;

import com.angubaidullin.dto.*;
import com.angubaidullin.entity.Account;
import com.angubaidullin.exception.AccountNotFoundException;
import com.angubaidullin.repository.AccountRepository;
import lombok.AllArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
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

        if (accountCreateDTO.getRoles() != null && accountCreateDTO.getRoles().size() > 0) {
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
    public AccountResponseUserDTO getCurrentAccount(String email) {
        Optional<Account> optionalAccount = accountRepository.findByEmail(email);
        Account account = optionalAccount.orElseThrow(() -> new AccountNotFoundException("account with email: " + email + "is not found"));
        return new AccountResponseUserDTO(account);

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
                .orElseThrow(() -> new AccountNotFoundException("account with id: " + email + "is not found"));
        billAddDTO.getBills().forEach(account::addBill);
        accountRepository.save(account);
        return new AccountResponseUserDTO(account);
    }

    @Transactional(readOnly = true)
    public List<KafkaDTO> getAllForKafka() {
        List<KafkaDTO> kafkaDTOS = accountRepository.findAll().stream()
                .peek(account -> Hibernate.initialize(account.getBills()))
                .map(KafkaDTO::new)
                .toList();
        return kafkaDTOS;
    }


    public Optional<Account> findByEmail(String email) {
        return accountRepository.findByEmail(email);
    }

    public Account registerOAuthUser(AccountRegistrationDTO registrationDTO) {
        Account account = new Account();
        account.setName(registrationDTO.getName());
        account.setEmail(registrationDTO.getEmail());
        account.addRole("ROLE_USER");
        account.setPassword(null);

        return accountRepository.save(account);
    }

    public Map<String, Object> getGoogleUserInfo(String code) {
        String tokenUrl = "https://oauth2.googleapis.com/token";
        String userInfoUrl = "https://www.googleapis.com/oauth2/v3/userinfo";

        // 1. Обменять code на access_token
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String body = "code=" + code +
                "&client_id=888642004818-re5nq1bajctgadec2j3gjnfs78rdvg8p.apps.googleusercontent.com" +
                "&client_secret=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" +
                "&redirect_uri=http://localhost:8080/login/oauth2/code/google" +
                "&grant_type=authorization_code";

        HttpEntity<String> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> tokenResponse = restTemplate.exchange(tokenUrl, HttpMethod.POST, request, Map.class);

        String accessToken = (String) tokenResponse.getBody().get("access_token");

        // 2. Получить информацию о пользователе
        HttpHeaders userInfoHeaders = new HttpHeaders();
        userInfoHeaders.setBearerAuth(accessToken);
        HttpEntity<String> userInfoRequest = new HttpEntity<>(userInfoHeaders);

        ResponseEntity<Map> userInfoResponse = restTemplate.exchange(userInfoUrl, HttpMethod.GET, userInfoRequest, Map.class);

        return userInfoResponse.getBody();
    }
}


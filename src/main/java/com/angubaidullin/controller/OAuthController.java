package com.angubaidullin.controller;

import com.angubaidullin.dto.AccountRegistrationDTO;
import com.angubaidullin.entity.Account;
import com.angubaidullin.service.AccountService;
import com.angubaidullin.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/oauth2")
@RequiredArgsConstructor
public class OAuthController {

    private final AccountService accountService;
    private final JWTUtil jwtUtil;

    @PostMapping("/callback/google")
    public ResponseEntity<String> handleGoogleCallback(@RequestBody Map<String, String> requestBody) {
        String code = requestBody.get("code");

        // Получение информации о пользователе через OAuth2
        Map<String, Object> userInfo = accountService.getGoogleUserInfo(code);

        String email = (String) userInfo.get("email");
        String name = (String) userInfo.get("name");

        // Проверка существования пользователя
        Account account = accountService.findByEmail(email)
                .orElseGet(() -> {
                    // Регистрация нового пользователя
                    AccountRegistrationDTO registrationDTO = new AccountRegistrationDTO();
                    registrationDTO.setName(name);
                    registrationDTO.setEmail(email);
                    registrationDTO.setPassword(null); // Пароля нет
                    return accountService.registerOAuthUser(registrationDTO);
                });

        // Генерация JWT токена
        return ResponseEntity.ok(jwtUtil.generateToken(account.getEmail()));
    }
}

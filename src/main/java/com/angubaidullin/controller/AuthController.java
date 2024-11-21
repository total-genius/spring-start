package com.angubaidullin.controller;

import com.angubaidullin.dto.AccountRegistrationDTO;
import com.angubaidullin.dto.AccountResponseAdminDTO;
import com.angubaidullin.dto.LoginRequest;
import com.angubaidullin.service.AccountService;
import com.angubaidullin.util.JWTUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final AccountService accountService;

    //Вход
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );

            User user = (User) authentication.getPrincipal();
            return ResponseEntity.ok(jwtUtil.generateToken(user.getUsername()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }

    }

    //Регистрация пользователя
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody AccountRegistrationDTO accountRegistrationDTO) {
        AccountResponseAdminDTO register = accountService.register(accountRegistrationDTO);
        return ResponseEntity.ok("Register successful:\n" + register);
    }
}

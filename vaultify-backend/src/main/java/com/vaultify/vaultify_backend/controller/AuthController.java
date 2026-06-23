package com.vaultify.vaultify_backend.controller;

import com.vaultify.vaultify_backend.dto.LoginRequest;
import com.vaultify.vaultify_backend.dto.LoginResponse;
import com.vaultify.vaultify_backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}

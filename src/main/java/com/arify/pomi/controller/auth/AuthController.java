package com.arify.pomi.controller.auth;

import com.arify.pomi.service.AuthService;
import lombok.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {

        String token = authService.login(
                request.getUsername(),
                request.getPassword());

        return new LoginResponse(token);
    }

    @Getter
    @Setter
    static class LoginRequest {
        private String username;
        private String password;
    }

    record LoginResponse(String token) {
    }
}
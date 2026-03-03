package com.arify.pomi.service;

import com.arify.pomi.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import com.arify.pomi.security.CustomUserDetailsService;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public String login(String username, String password) {

        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        username, password));

        UserDetails user = userDetailsService.loadUserByUsername(username);

        return jwtService.generateToken(user);
    }
}
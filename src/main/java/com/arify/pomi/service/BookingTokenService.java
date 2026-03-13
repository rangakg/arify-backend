package com.arify.pomi.service;

import lombok.RequiredArgsConstructor;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingTokenService {

    private final JdbcTemplate jdbc;

    public String createToken(String phone) {

        String token = UUID.randomUUID().toString();

        jdbc.update(
                "INSERT INTO booking_tokens(token, phone, expires_at) VALUES (?, ?, now() + interval '15 minutes')",
                token,
                phone);

        return token;
    }

    public String getPhone(String token) {

        return jdbc.queryForObject(
                "SELECT phone FROM booking_tokens WHERE token=? AND expires_at > now()",
                String.class,
                token);
    }
}
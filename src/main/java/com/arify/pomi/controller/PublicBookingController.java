package com.arify.pomi.controller;

import com.arify.pomi.service.BookingTokenService;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicBookingController {

    private final BookingTokenService tokenService;

    @GetMapping("/session")
    public Map<String, String> session(@RequestParam String t) {

        String phone = tokenService.getPhone(t);

        return Map.of("phone", phone);
    }
}
package com.arify.pomi.controller;

import com.arify.pomi.entity.SlotEntity;
import com.arify.pomi.repository.SlotRepository;
import com.arify.pomi.service.BookingTokenService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicBookingController {

    private final BookingTokenService tokenService;

    private final SlotRepository slotRepo; // ← ADD THIS

    @GetMapping("/session")
    public Map<String, String> session(@RequestParam String t) {
        String phone = tokenService.getPhone(t);
        return Map.of("phone", phone);
    }

    @GetMapping("/slots")
    public List<SlotEntity> getSlots(
            @RequestParam Long doctorId,
            @RequestParam String date) {

        LocalDate d = LocalDate.parse(date);

        return slotRepo.findAvailableSlots(doctorId, d);
    }
}
package com.arify.pomi.controller;

import com.arify.pomi.entity.SlotEntity;
import com.arify.pomi.repository.SlotRepository;
import com.arify.pomi.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/public/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final SlotRepository slotRepo; // ✅ FIX

    // -----------------------
    // PREVIEW (NO DB WRITE)
    // -----------------------
    @GetMapping("/preview")
    public Map<String, String> preview(@RequestParam Long slotId) {

        SlotEntity slot = slotRepo.findById(slotId)
                .orElseThrow(() -> new RuntimeException("Invalid slot"));

        return Map.of(
                "doctor", slot.getDoctor().getName(),
                "date", slot.getSlotTime().toLocalDate().toString(),
                "time", slot.getSlotTime().toLocalTime().toString());
    }

    // -----------------------
    // CONFIRM BOOKING
    // -----------------------
    @PostMapping
    public Map<String, String> confirmBooking(@RequestBody Map<String, String> req) {

        String phone = req.get("phone");
        Long slotId = Long.valueOf(req.get("slotId"));

        appointmentService.confirmBooking(phone, slotId);

        return Map.of("message", "Appointment locked");
    }
}
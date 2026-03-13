package com.arify.pomi.controller;

import com.arify.pomi.dto.AppointmentRequest;
import com.arify.pomi.entity.AppointmentEntity;
import com.arify.pomi.entity.AppointmentStatus;
import com.arify.pomi.entity.SlotEntity;
import com.arify.pomi.repository.SlotRepository;
import com.arify.pomi.service.BookingTokenService;
import com.arify.pomi.repository.AppointmentRepository;

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
    private final AppointmentRepository appointmentRepo;

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

    @PostMapping("/appointments")
    public Map<String, String> createAppointment(
            @RequestBody AppointmentRequest req) {

        String phone = tokenService.getPhone(req.getToken());

        SlotEntity slot = slotRepo.findById(req.getSlotId())
                .orElseThrow();

        AppointmentEntity a = new AppointmentEntity();

        a.setPhone(phone);
        a.setDoctor(slot.getDoctor());
        a.setSlot(slot);
        a.setStatus(AppointmentStatus.LOCKED);

        appointmentRepo.save(a);

        return Map.of("status", "success");
    }
}
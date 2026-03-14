package com.arify.pomi.controller;

import com.arify.pomi.dto.AppointmentRequest;
import com.arify.pomi.entity.AppointmentEntity;
import com.arify.pomi.entity.AppointmentStatus;
import com.arify.pomi.entity.SlotEntity;
import com.arify.pomi.entity.DoctorEntity;

import com.arify.pomi.repository.SlotRepository;
import com.arify.pomi.repository.AppointmentRepository;
import com.arify.pomi.repository.DoctorRepository;

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

    private final SlotRepository slotRepo;

    private final AppointmentRepository appointmentRepo;

    private final DoctorRepository doctorRepo;

    // ----------------------------------------------------
    // SESSION (validate booking token)
    // ----------------------------------------------------

    @GetMapping("/session")
    public Map<String, String> session(@RequestParam String t) {

        String phone = tokenService.getPhone(t);

        return Map.of("phone", phone);
    }

    // ----------------------------------------------------
    // LIST DOCTORS
    // ----------------------------------------------------

    @GetMapping("/doctors")
    public List<DoctorEntity> getDoctors() {

        return doctorRepo.findAll();
    }

    // ----------------------------------------------------
    // LIST AVAILABLE SLOTS
    // ----------------------------------------------------

    @GetMapping("/slots")
    public List<SlotEntity> getSlots(
            @RequestParam Long doctorId,
            @RequestParam String date) {

        LocalDate d = LocalDate.parse(date);

        return slotRepo.findAvailableSlots(doctorId, d);
    }

    // ----------------------------------------------------
    // CREATE APPOINTMENT (LOCK SLOT)
    // ----------------------------------------------------

    @PostMapping("/appointments")
    public Map<String, String> createAppointment(
            @RequestBody AppointmentRequest req) {

        String phone = tokenService.getPhone(req.getToken());

        SlotEntity slot = slotRepo
                .findById(req.getSlotId())
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
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

    // ----------------------------------------------------
    // GET APPOINTMENT BY TOKEN
    // ----------------------------------------------------

    // ----------------------------------------------------
    // GET APPOINTMENT BY TOKEN
    // ----------------------------------------------------

    @GetMapping("/appointments/by-token")
    public AppointmentEntity getAppointmentByToken(@RequestParam String token) {

        String phone = tokenService.getPhone(token);

        return appointmentRepo.findByPhone(phone)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
    }

    @DeleteMapping("/appointments/cancel")
    public Map<String, String> cancelAppointment(@RequestParam String token) {

        String phone = tokenService.getPhone(token);

        AppointmentEntity appt = appointmentRepo.findByPhone(phone)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        // ✅ Allow delete ONLY for LOCKED
        if (appt.getStatus() != AppointmentStatus.LOCKED) {
            throw new RuntimeException("Only LOCKED appointments can be cancelled");
        }

        // ✅ DELETE (no slot manipulation needed in your system)
        appointmentRepo.delete(appt);

        return Map.of("message", "Appointment cancelled");
    }
}
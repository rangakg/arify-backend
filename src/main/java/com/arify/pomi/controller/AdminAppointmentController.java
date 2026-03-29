package com.arify.pomi.controller;

import com.arify.pomi.entity.AppointmentEntity;
import com.arify.pomi.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/appointments")
@RequiredArgsConstructor
public class AdminAppointmentController {

    private final AppointmentRepository appointmentRepo;

    @GetMapping
    public List<Map<String, String>> getAllAppointments() {

        return appointmentRepo.findAll().stream().map(a -> Map.of(
                "phone", a.getPhone(),
                "name", a.getUser().getName(),
                "doctor", a.getDoctor().getName(),
                "slotTime", a.getSlot().getSlotTime().toString(),
                "status", a.getStatus().name())).toList();
    }
}
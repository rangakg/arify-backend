package com.arify.pomi.controller;

import com.arify.pomi.entity.*;
import com.arify.pomi.repository.*;
import com.arify.pomi.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneId;
import java.util.Map;

@RestController
@RequestMapping("/api/public/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final SlotRepository slotRepo;
    private final AppointmentRepository appointmentRepo;

    private static final ZoneId IST = ZoneId.of("Asia/Kolkata");

    // -----------------------
    // PREVIEW (NO DB WRITE)
    // -----------------------
    @GetMapping("/preview")
    public Map<String, String> preview(@RequestParam Long slotId) {

        SlotEntity slot = slotRepo.findById(slotId)
                .orElseThrow(() -> new RuntimeException("Invalid slot"));

        return Map.of(
                "doctor", slot.getDoctor().getName(),
                "date", slot.getSlotTime().atZoneSameInstant(IST).toLocalDate().toString(),
                "time", slot.getSlotTime().atZoneSameInstant(IST).toLocalTime().toString());
    }

    // -----------------------
    // CONFIRM BOOKING (LOCK)
    // -----------------------
    @PostMapping
    public Map<String, Object> confirmBooking(@RequestBody Map<String, String> req) {

        String phone = req.get("phone");
        Long slotId = Long.valueOf(req.get("slotId"));

        appointmentService.confirmBooking(phone, slotId);

        // 🔥 Fetch created appointment
        AppointmentEntity appt = appointmentRepo.findById(phone)
                .orElseThrow();

        return Map.of(
                "phone", appt.getPhone(),
                "doctorId", appt.getDoctor().getId(),
                "doctorName", appt.getDoctor().getName(),
                "slotId", appt.getSlot().getId(),
                "slotTime", appt.getSlot().getSlotTime().toString(),
                "status", appt.getStatus().name());
    }

    // -----------------------
    // CHANGE SLOT
    // -----------------------
    @PostMapping("/{phone}/change-slot")
    public Map<String, String> changeSlot(
            @PathVariable String phone,
            @RequestParam Long slotId) {

        AppointmentEntity appt = appointmentRepo.findById(phone)
                .orElseThrow();

        // 🔓 release old slot
        SlotEntity oldSlot = appt.getSlot();
        oldSlot.setStatus(SlotStatus.AVAILABLE);
        slotRepo.save(oldSlot);

        // 🔒 lock new slot
        SlotEntity newSlot = slotRepo.findById(slotId)
                .orElseThrow();

        if (newSlot.getStatus() != SlotStatus.AVAILABLE) {
            throw new RuntimeException("Slot not available");
        }

        newSlot.setStatus(SlotStatus.LOCKED);
        slotRepo.save(newSlot);

        // 🔄 update appointment
        appt.setSlot(newSlot);
        appointmentRepo.save(appt);

        return Map.of("message", "Slot updated");
    }

    // -----------------------
    // CANCEL (OPTIONAL)
    // -----------------------
    @PostMapping("/{phone}/cancel")
    public Map<String, String> cancel(@PathVariable String phone) {

        AppointmentEntity appt = appointmentRepo.findById(phone)
                .orElseThrow();

        // release slot
        SlotEntity slot = appt.getSlot();
        slot.setStatus(SlotStatus.AVAILABLE);
        slotRepo.save(slot);

        appt.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepo.save(appt);

        return Map.of("message", "Appointment cancelled");
    }

    @PostMapping("/update")
public Map<String, String> updateSlot(@RequestBody Map<String, String> req) {

    String phone = req.get("phone");
    Long slotId = Long.valueOf(req.get("slotId"));

    appointmentService.updateSlot(phone, slotId);

    return Map.of("message", "Slot updated");
}
}

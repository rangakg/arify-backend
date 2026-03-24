package com.arify.pomi.service;

import com.arify.pomi.entity.*;
import com.arify.pomi.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepo;
    private final SlotRepository slotRepo;

    // -----------------------
    // CONFIRM BOOKING (STEP 6)
    // -----------------------
    @Transactional
    public void confirmBooking(String phone, Long slotId) {

        // ❌ prevent duplicate (phone is PK)
        if (appointmentRepo.existsById(phone)) {
            throw new RuntimeException("Appointment already exists");
        }

        // 1. Get slot
        SlotEntity slot = slotRepo.findById(slotId)
                .orElseThrow(() -> new RuntimeException("Invalid slot"));

        // 2. Ensure slot still available
        if (slot.getStatus() != SlotStatus.AVAILABLE) {
            throw new RuntimeException("Slot already taken");
        }

        // 3. Create appointment (LOCKED, no orderId)
        AppointmentEntity appt = AppointmentEntity.builder()
                .phone(phone)
                .doctor(slot.getDoctor())
                .slot(slot)
                .status(AppointmentStatus.LOCKED)
                .createdAt(OffsetDateTime.now())
                .orderId(null)
                .build();

        appointmentRepo.save(appt);

        // 4. Lock slot
        slot.setStatus(SlotStatus.LOCKED);
        slotRepo.save(slot);
    }

    // -----------------------
    // PAYMENT SUCCESS
    // -----------------------
    @Transactional
    public void markPaymentSuccess(String phone, String orderId) {

        AppointmentEntity appt = appointmentRepo.findById(phone)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        appt.setStatus(AppointmentStatus.PAID);
        appt.setOrderId(orderId);
        appt.setConfirmedAt(OffsetDateTime.now());

        // slot → BOOKED
        SlotEntity slot = appt.getSlot();
        slot.setStatus(SlotStatus.BOOKED);

        slotRepo.save(slot);
        appointmentRepo.save(appt);
    }

    // -----------------------
    // PAYMENT FAILED
    // -----------------------
    @Transactional
    public void markPaymentFailed(String phone) {

        AppointmentEntity appt = appointmentRepo.findById(phone)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        appt.setStatus(AppointmentStatus.CANCELLED);

        // release slot
        SlotEntity slot = appt.getSlot();
        slot.setStatus(SlotStatus.AVAILABLE);

        slotRepo.save(slot);
        appointmentRepo.save(appt);
    }

    @Transactional
public void updateSlot(String phone, Long newSlotId) {

    AppointmentEntity appt = appointmentRepo.findById(phone)
            .orElseThrow(() -> new RuntimeException("Appointment not found"));

    SlotEntity oldSlot = appt.getSlot();

    SlotEntity newSlot = slotRepo.findById(newSlotId)
            .orElseThrow(() -> new RuntimeException("Invalid slot"));

    // prevent double booking
    if (newSlot.getStatus() != SlotStatus.AVAILABLE) {
        throw new RuntimeException("Slot not available");
    }

    // free old slot
    oldSlot.setStatus(SlotStatus.AVAILABLE);

    // lock new slot
    newSlot.setStatus(SlotStatus.LOCKED);

    // update appointment
    appt.setSlot(newSlot);

    slotRepo.save(oldSlot);
    slotRepo.save(newSlot);
    appointmentRepo.save(appt);
}
}
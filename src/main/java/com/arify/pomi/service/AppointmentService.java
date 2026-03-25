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
    // CONFIRM BOOKING
    // -----------------------
    @Transactional
    public void confirmBooking(String phone, Long slotId) {

        // 1. Get slot
        SlotEntity newSlot = slotRepo.findById(slotId)
                .orElseThrow(() -> new RuntimeException("Invalid slot"));

        // 2. Find existing appointment
        AppointmentEntity existing = appointmentRepo
                .findTopByPhoneOrderByCreatedAtDesc(phone)
                .orElse(null);

        if (existing != null) {

            // 🚫 If already PAID → block
            if (existing.getStatus() == AppointmentStatus.PAID) {
                throw new RuntimeException("Appointment already completed");
            }

            // 🔄 If LOCKED → update slot
            if (existing.getStatus() == AppointmentStatus.LOCKED) {

                SlotEntity oldSlot = existing.getSlot();

                // release old slot
                if (oldSlot != null) {
                    oldSlot.setStatus(SlotStatus.AVAILABLE);
                    slotRepo.save(oldSlot);
                }

                // ensure new slot available
                if (newSlot.getStatus() != SlotStatus.AVAILABLE) {
                    throw new RuntimeException("Slot already taken");
                }

                // lock new slot
                newSlot.setStatus(SlotStatus.LOCKED);
                slotRepo.save(newSlot);

                // update appointment
                existing.setSlot(newSlot);
                existing.setDoctor(newSlot.getDoctor());
                existing.setCreatedAt(OffsetDateTime.now());

                appointmentRepo.save(existing);

                return;
            }
        }

        // -----------------------
        // NEW BOOKING
        // -----------------------

        // ensure slot available
        if (newSlot.getStatus() != SlotStatus.AVAILABLE) {
            throw new RuntimeException("Slot already taken");
        }

        // lock slot
        newSlot.setStatus(SlotStatus.LOCKED);
        slotRepo.save(newSlot);

        // create appointment
        AppointmentEntity appt = AppointmentEntity.builder()
                .phone(phone)
                .doctor(newSlot.getDoctor())
                .slot(newSlot)
                .status(AppointmentStatus.LOCKED)
                .createdAt(OffsetDateTime.now())
                .orderId(null)
                .build();

        appointmentRepo.save(appt);
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

    // -----------------------
    // UPDATE SLOT (MANUAL CHANGE)
    // -----------------------
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
        appt.setDoctor(newSlot.getDoctor());

        slotRepo.save(oldSlot);
        slotRepo.save(newSlot);
        appointmentRepo.save(appt);
    }
}
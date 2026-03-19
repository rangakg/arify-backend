package com.arify.pomi.service;

import com.arify.pomi.entity.*;
import com.arify.pomi.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SlotScheduler {

    private final DoctorRepository doctorRepository;
    private final SlotRepository slotRepository;

    // ⏱ DEV → every 5 minutes
    // ⏱ PROD → "0 0 1 * * *" (1 AM daily)
    @Scheduled(cron = "0 */5 * * * *")
    @Transactional
    public void refreshSlots() {

        System.out.println("🕒 CRON STARTED");

        // ✅ 1. Delete ONLY AVAILABLE slots
        slotRepository.deleteByStatus(SlotStatus.AVAILABLE);

        System.out.println("🧹 Old available slots deleted");

        // ✅ 2. Generate new slots
        List<DoctorEntity> doctors = doctorRepository.findAll();

        LocalDate today = LocalDate.now();

        for (DoctorEntity doctor : doctors) {

            for (int d = 0; d < 7; d++) {

                LocalDate date = today.plusDays(d);

                createSlots(doctor, date, 9, 13);
                createSlots(doctor, date, 14, 17);
                createSlots(doctor, date, 17, 20);
            }
        }

        System.out.println("✅ Slots regenerated");
    }

    private void createSlots(DoctorEntity doctor, LocalDate date, int startHour, int endHour) {

        ZoneOffset offset = ZoneOffset.UTC;

        OffsetDateTime start = date.atTime(startHour, 0).atOffset(offset);
        OffsetDateTime end = date.atTime(endHour, 0).atOffset(offset);

        while (start.isBefore(end)) {

            boolean exists = slotRepository
                    .existsByDoctorAndSlotTime(doctor, start);

            if (!exists) {

                SlotEntity slot = new SlotEntity();
                slot.setDoctor(doctor);
                slot.setSlotTime(start);
                slot.setStatus(SlotStatus.AVAILABLE);

                slotRepository.save(slot);
            }

            start = start.plusMinutes(15);
        }
    }
}
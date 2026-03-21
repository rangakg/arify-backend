package com.arify.pomi.service;

import com.arify.pomi.entity.DoctorEntity;
import com.arify.pomi.entity.SlotEntity;
import com.arify.pomi.entity.SlotStatus;
import com.arify.pomi.repository.DoctorRepository;
import com.arify.pomi.repository.SlotRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.List;

@Service
public class SlotService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private SlotRepository slotRepository;

    // ✅ Define IST once (BEST PRACTICE)
    private static final ZoneId IST = ZoneId.of("Asia/Kolkata");

    // -----------------------
    // GENERATE SLOTS
    // -----------------------

    public void generateSlots(int days) {

        List<DoctorEntity> doctors = doctorRepository.findAll();

        System.out.println("Doctors count: " + doctors.size());

        // ✅ Always use IST date
        LocalDate today = LocalDate.now(IST);

        for (DoctorEntity doctor : doctors) {

            System.out.println("Generating for doctor: " + doctor.getId());

            for (int d = 0; d < days; d++) {

                LocalDate date = today.plusDays(d);

                createSlots(doctor, date, 9, 13); // Morning
                createSlots(doctor, date, 14, 17); // Afternoon
                createSlots(doctor, date, 17, 20); // Evening
            }
        }
    }

    // -----------------------
    // CREATE SLOTS
    // -----------------------

    private void createSlots(DoctorEntity doctor, LocalDate date, int startHour, int endHour) {

        LocalDateTime start = date.atTime(startHour, 0);
        LocalDateTime end = date.atTime(endHour, 0);

        while (start.isBefore(end)) {

            // ✅ Correct timezone conversion (NO manual offset)
            ZonedDateTime zdt = start.atZone(IST);
            OffsetDateTime slotTime = zdt.toOffsetDateTime();

            boolean exists = slotRepository
                    .existsByDoctorAndSlotTime(doctor, slotTime);

            if (!exists) {

                SlotEntity slot = new SlotEntity();
                slot.setDoctor(doctor);
                slot.setSlotTime(slotTime);
                slot.setStatus(SlotStatus.AVAILABLE);

                slotRepository.save(slot);
            }

            start = start.plusMinutes(15);

        }
    }
}
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

    // -----------------------
    // GENERATE SLOTS
    // -----------------------

    public void generateSlots(int days) {

        List<DoctorEntity> doctors = doctorRepository.findAll();

        System.out.println("Doctors count: " + doctors.size());

        LocalDate today = LocalDate.now();

        for (DoctorEntity doctor : doctors) {

            System.out.println("Generating for doctor: " + doctor.getId());

            for (int d = 0; d < days; d++) {

                LocalDate date = today.plusDays(d);

                createSlots(doctor, date, 9, 13);
                createSlots(doctor, date, 14, 17);
                createSlots(doctor, date, 17, 20);
            }
        }
    }

    // -----------------------
    // CREATE SLOTS
    // -----------------------

    private void createSlots(DoctorEntity doctor, LocalDate date, int startHour, int endHour) {

        ZoneOffset offset = ZoneOffset.UTC; // ✅ FIXED TIMEZONE

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

                System.out.println("✅ Created slot: " + start);
            }

            start = start.plusMinutes(15);
        }
    }
}
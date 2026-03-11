package com.arify.pomi.service;

import com.arify.pomi.entity.*;
import com.arify.pomi.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SlotGeneratorService {

    private final DoctorScheduleRepository scheduleRepo;
    private final SlotRepository slotRepo;
    private final DoctorRepository doctorRepo;

    public void generateSlots(Long doctorId, int days) {

        DoctorEntity doctor = doctorRepo.findById(doctorId).orElseThrow();

        List<DoctorSchedule> schedules = scheduleRepo.findByDoctorIdAndActiveTrue(doctorId);

        for (int i = 0; i < days; i++) {

            LocalDate date = LocalDate.now().plusDays(i);

            int dow = date.getDayOfWeek().getValue() % 7;

            for (DoctorSchedule s : schedules) {

                if (s.getDayOfWeek() != dow)
                    continue;

                LocalTime time = s.getStartTime();

                while (time.isBefore(s.getEndTime())) {

                    OffsetDateTime slotTime = LocalDateTime.of(date, time)
                            .atOffset(ZoneOffset.UTC);
                    boolean exists = slotRepo.existsByDoctorAndSlotTime(
                            doctor,
                            slotTime);

                    if (!exists) {

                        SlotEntity slot = new SlotEntity();

                        slot.setDoctor(doctor);
                        slot.setSlotTime(slotTime);
                        slot.setStatus(SlotStatus.AVAILABLE);

                        slotRepo.save(slot);
                    }

                    time = time.plusMinutes(
                            s.getSlotDurationMinutes());
                }
            }
        }
    }
}
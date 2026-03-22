package com.arify.pomi.repository;

import com.arify.pomi.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.OffsetDateTime;
//import java.time.LocalDateTime;
import java.util.List;

public interface SlotRepository extends JpaRepository<SlotEntity, Long> {

        List<SlotEntity> findByDoctorIdAndStatus(Long doctorId, SlotStatus status);

        boolean existsByDoctorAndSlotTime(
                        DoctorEntity doctor,
                        OffsetDateTime slotTime);

        @Query("""
                        SELECT s
                        FROM SlotEntity s
                        WHERE s.doctor.id = :doctorId
                        AND DATE(s.slotTime) = :date
                        AND s.status = 'AVAILABLE'
                        ORDER BY s.slotTime
                        """)
        List<SlotEntity> findAvailableSlots(
                        Long doctorId,
                        LocalDate date);

        void deleteByStatus(SlotStatus status);
}

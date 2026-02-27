package com.arify.pomi.repository;

import com.arify.pomi.entity.SlotEntity;
import com.arify.pomi.entity.SlotStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SlotRepository extends JpaRepository<SlotEntity, Long> {
    List<SlotEntity> findByDoctorIdAndStatus(Long doctorId, SlotStatus status);
}
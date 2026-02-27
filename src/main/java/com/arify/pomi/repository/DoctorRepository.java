package com.arify.pomi.repository;

import com.arify.pomi.entity.DoctorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DoctorRepository extends JpaRepository<DoctorEntity, Long> {
    List<DoctorEntity> findByServiceIdAndActiveTrue(Long serviceId);
}
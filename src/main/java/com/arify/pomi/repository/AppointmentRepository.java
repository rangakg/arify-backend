package com.arify.pomi.repository;

import com.arify.pomi.entity.AppointmentEntity;
import com.arify.pomi.entity.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<AppointmentEntity, String> {

    boolean existsByPhoneAndStatus(String phone, AppointmentStatus status);

    Optional<AppointmentEntity> findTopByPhoneOrderByCreatedAtDesc(String phone);
}
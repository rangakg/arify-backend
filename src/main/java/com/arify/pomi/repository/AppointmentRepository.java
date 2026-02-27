package com.arify.pomi.repository;

import com.arify.pomi.entity.AppointmentEntity;
import com.arify.pomi.entity.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRepository extends JpaRepository<AppointmentEntity, Long> {

    boolean existsByUserPhoneAndStatus(String phone, AppointmentStatus status);

}
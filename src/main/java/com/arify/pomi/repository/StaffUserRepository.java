package com.arify.pomi.repository;

import com.arify.pomi.entity.StaffUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StaffUserRepository
        extends JpaRepository<StaffUserEntity, Long> {

    Optional<StaffUserEntity> findByUsername(String username);
}
package com.arify.pomi.repository;

import com.arify.pomi.entity.WhatsappSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WhatsappSessionRepository extends JpaRepository<WhatsappSessionEntity, String> {
}
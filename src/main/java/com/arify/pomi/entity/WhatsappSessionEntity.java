package com.arify.pomi.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "whatsapp_sessions")
public class WhatsappSessionEntity {

    @Id
    private String phone;

    @Column(nullable = false)
    private String state;

    @Column(columnDefinition = "jsonb")
    private String tempData;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    // getters & setters
}
package com.arify.pomi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(name = "appointments", uniqueConstraints = @UniqueConstraint(columnNames = { "slot_id" }))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class AppointmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_phone", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private DoctorEntity doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "slot_id", nullable = false)
    private SlotEntity slot;

    @Enumerated(EnumType.STRING)
    private AppointmentStatus status = AppointmentStatus.CREATED;

    @Column(name = "created_at")
    private OffsetDateTime createdAt = OffsetDateTime.now();

    // getters & setters
}
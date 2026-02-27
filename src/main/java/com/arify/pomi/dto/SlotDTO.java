package com.arify.pomi.dto;

import java.time.OffsetDateTime;

public class SlotDTO {

    private Long id;
    private OffsetDateTime slotTime;

    public SlotDTO(Long id, OffsetDateTime slotTime) {
        this.id = id;
        this.slotTime = slotTime;
    }

    public Long getId() {
        return id;
    }

    public OffsetDateTime getSlotTime() {
        return slotTime;
    }
}
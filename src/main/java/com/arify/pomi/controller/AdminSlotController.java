package com.arify.pomi.controller;

import com.arify.pomi.service.SlotGeneratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/slots")
@RequiredArgsConstructor
public class AdminSlotController {

    private final SlotGeneratorService slotGeneratorService;

    @PostMapping("/generate/{doctorId}")
    public void generateSlots(
            @PathVariable Long doctorId,
            @RequestParam(defaultValue = "30") int days) {

        slotGeneratorService.generateSlots(doctorId, days);
    }
}
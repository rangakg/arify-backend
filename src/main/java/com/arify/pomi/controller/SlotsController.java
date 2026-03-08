package com.arify.pomi.controller;

import com.arify.pomi.entity.SlotEntity;
import com.arify.pomi.repository.SlotRepository;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/slots")
public class SlotsController {

    private final SlotRepository slotRepository;

    public SlotsController(SlotRepository slotRepository) {
        this.slotRepository = slotRepository;
    }

    // List all slots
    @GetMapping
    public List<SlotEntity> getAllSlots() {
        return slotRepository.findAll();
    }

    // Create slot
    @PostMapping
    public SlotEntity createSlot(@RequestBody SlotEntity slot) {
        return slotRepository.save(slot);
    }

    // Delete slot
    @DeleteMapping("/{id}")
    public void deleteSlot(@PathVariable Long id) {
        slotRepository.deleteById(id);
    }
}
package com.arify.pomi.controller;

import com.arify.pomi.entity.SlotEntity;
import com.arify.pomi.repository.SlotRepository;
import com.arify.pomi.repository.DoctorRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.arify.pomi.entity.SlotStatus;

@RestController
@RequestMapping("/api/slots")
public class SlotsController {

    private final SlotRepository slotRepo;
    private final DoctorRepository doctorRepo;

    public SlotsController(SlotRepository slotRepo,
            DoctorRepository doctorRepo) {
        this.slotRepo = slotRepo;
        this.doctorRepo = doctorRepo;
    }

    @GetMapping
    public List<SlotEntity> list() {
        return slotRepo.findAll();
    }

    @PostMapping
    public SlotEntity createSlot(@RequestBody SlotEntity slot) {

        slot.setDoctor(
                doctorRepo.findById(
                        slot.getDoctor().getId()).orElseThrow());

        slot.setStatus(SlotStatus.AVAILABLE);

        return slotRepo.save(slot);
    }

    @PutMapping("/{id}")
    public SlotEntity update(@PathVariable Long id,
            @RequestBody SlotEntity updated) {

        SlotEntity slot = slotRepo.findById(id).orElseThrow();

        slot.setSlotTime(updated.getSlotTime());
        slot.setStatus(updated.getStatus());

        return slotRepo.save(slot);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        slotRepo.deleteById(id);
    }
}
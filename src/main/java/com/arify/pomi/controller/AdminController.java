package com.arify.pomi.controller;

import com.arify.pomi.entity.ServiceEntity;
import com.arify.pomi.repository.ServiceRepository;
import com.arify.pomi.service.SlotService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/services")
public class AdminController {

    private final ServiceRepository serviceRepo;

    public AdminController(ServiceRepository serviceRepo) {
        this.serviceRepo = serviceRepo;
    }

    @GetMapping
    public List<ServiceEntity> getAll() {
        return serviceRepo.findAll();
    }

    @PostMapping
    public ServiceEntity create(@RequestBody ServiceEntity service) {
        service.setActive(true);
        return serviceRepo.save(service);
    }

    @PutMapping("/{id}")
    public ServiceEntity update(@PathVariable Long id,
            @RequestBody ServiceEntity updated) {

        ServiceEntity s = serviceRepo.findById(id).orElseThrow();

        s.setName(updated.getName());
        s.setActive(updated.getActive());

        return serviceRepo.save(s);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        serviceRepo.deleteById(id);
    }

    @PostMapping("/slots/refresh")
    public String refreshSlots(
            @RequestParam(defaultValue = "7") int days) {

        int deleted = cleanupUnusedSlots();

        slotService.generateSlots(days);

        return "Slots refreshed | Deleted: " + deleted + " | Generated for " + days + " days";
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private SlotService slotService;

    private int cleanupUnusedSlots() {

        String sql = """
                    DELETE FROM slots s
                    WHERE s.id NOT IN (
                        SELECT slot_id FROM appointments
                        UNION
                        SELECT slot_id FROM appointments_history
                    )
                    AND s.slot_time >= CURRENT_DATE
                """;

        return jdbcTemplate.update(sql);
    }
}
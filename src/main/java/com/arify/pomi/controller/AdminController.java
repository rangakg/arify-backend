package com.arify.pomi.controller;

import com.arify.pomi.entity.ServiceEntity;
import com.arify.pomi.repository.ServiceRepository;
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
}
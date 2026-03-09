package com.arify.pomi.controller;

import com.arify.pomi.entity.DoctorEntity;
import com.arify.pomi.repository.DoctorRepository;
import com.arify.pomi.repository.ServiceRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
public class DoctorsController {

    private final DoctorRepository doctorRepo;
    private final ServiceRepository serviceRepo;

    public DoctorsController(DoctorRepository doctorRepo,
            ServiceRepository serviceRepo) {
        this.doctorRepo = doctorRepo;
        this.serviceRepo = serviceRepo;
    }

    @GetMapping
    public List<DoctorEntity> list() {
        return doctorRepo.findAll();
    }

    @PostMapping
    public DoctorEntity create(@RequestBody DoctorEntity doctor) {

        doctor.setService(
                serviceRepo.findById(
                        doctor.getService().getId()).orElseThrow());

        doctor.setActive(true);

        return doctorRepo.save(doctor);
    }

    @PutMapping("/{id}")
    public DoctorEntity update(@PathVariable Long id,
            @RequestBody DoctorEntity updated) {

        DoctorEntity d = doctorRepo.findById(id).orElseThrow();

        d.setName(updated.getName());
        d.setPhone(updated.getPhone());
        d.setActive(updated.getActive());

        return doctorRepo.save(d);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        doctorRepo.deleteById(id);
    }
}
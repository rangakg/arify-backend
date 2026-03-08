package com.arify.pomi.controller;

import com.arify.pomi.entity.DoctorEntity;
import com.arify.pomi.repository.DoctorRepository;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
public class DoctorsController {

    private final DoctorRepository doctorRepository;

    public DoctorsController(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    @GetMapping
    public List<DoctorEntity> getAllDoctors() {
        return doctorRepository.findAll();
    }
}
package com.arify.pomi.dto;

public class DoctorDTO {

    private Long id;
    private String name;

    public DoctorDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
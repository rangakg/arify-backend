package com.arify.pomi.dto;

public class ServiceDTO {

    private Long id;
    private String name;

    public ServiceDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
}
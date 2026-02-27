package com.arify.pomi.controller;

import com.arify.pomi.dto.*;
import com.arify.pomi.service.BookingService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/services")
    public List<ServiceDTO> getServices() {
        return bookingService.getServices();
    }

    @GetMapping("/doctors")
    public List<DoctorDTO> getDoctors(@RequestParam Long serviceId) {
        return bookingService.getDoctors(serviceId);
    }

    @GetMapping("/slots")
    public List<SlotDTO> getSlots(@RequestParam Long doctorId) {
        return bookingService.getAvailableSlots(doctorId);
    }

    @PostMapping("/appointments")
    public void book(@RequestBody AppointmentRequestDTO request) {
        bookingService.bookAppointment(request);
    }
}
package com.arify.pomi.service;

import com.arify.pomi.dto.*;
import com.arify.pomi.entity.*;
import com.arify.pomi.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookingService {

    private final ServiceRepository serviceRepo;
    private final DoctorRepository doctorRepo;
    private final SlotRepository slotRepo;
    private final AppointmentRepository appointmentRepo;
    private final UserRepository userRepo;

    public BookingService(ServiceRepository serviceRepo,
            DoctorRepository doctorRepo,
            SlotRepository slotRepo,
            AppointmentRepository appointmentRepo,
            UserRepository userRepo) {
        this.serviceRepo = serviceRepo;
        this.doctorRepo = doctorRepo;
        this.slotRepo = slotRepo;
        this.appointmentRepo = appointmentRepo;
        this.userRepo = userRepo;
    }

    public List<ServiceDTO> getServices() {
        return serviceRepo.findAll()
                .stream()
                .filter(ServiceEntity::getActive)
                .map(s -> new ServiceDTO(s.getId(), s.getName()))
                .toList();
    }

    public List<DoctorDTO> getDoctors(Long serviceId) {
        return doctorRepo.findByServiceIdAndActiveTrue(serviceId)
                .stream()
                .map(d -> new DoctorDTO(d.getId(), d.getName()))
                .toList();
    }

    public List<SlotDTO> getAvailableSlots(Long doctorId) {
        return slotRepo.findByDoctorIdAndStatus(doctorId, SlotStatus.AVAILABLE)
                .stream()
                .map(s -> new SlotDTO(s.getId(), s.getSlotTime()))
                .toList();
    }

    @Transactional
    public void bookAppointment(AppointmentRequestDTO request) {

        UserEntity user = userRepo.findById(request.getUserPhone())
                .orElseThrow(() -> new RuntimeException("User not found"));

        DoctorEntity doctor = doctorRepo.findById(request.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        SlotEntity slot = slotRepo.findById(request.getSlotId())
                .orElseThrow(() -> new RuntimeException("Slot not found"));

        if (slot.getStatus() != SlotStatus.AVAILABLE) {
            throw new RuntimeException("Slot already booked");
        }

        AppointmentEntity appointment = new AppointmentEntity();
        appointment.setPhone(user.getPhone());
        appointment.setDoctor(doctor);
        appointment.setSlot(slot);
        appointment.setStatus(AppointmentStatus.CREATED);

        appointmentRepo.save(appointment);

        slot.setStatus(SlotStatus.BOOKED);
        slotRepo.save(slot);
    }
}
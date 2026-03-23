package com.arify.pomi.controller;

import com.arify.pomi.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/public/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    // -----------------------
    // CONFIRM BOOKING
    // -----------------------
    @PostMapping
    public Map<String, String> confirmBooking(@RequestBody Map<String, String> req) {

        String phone = req.get("phone");
        Long slotId = Long.valueOf(req.get("slotId"));

        appointmentService.confirmBooking(phone, slotId);

        return Map.of(
                "message", "Appointment locked. Proceed to payment.");
    }

    // -----------------------
    // PAYMENT SUCCESS (TEMP)
    // -----------------------
    @PostMapping("/success")
    public String paymentSuccess(@RequestParam String phone,
            @RequestParam String orderId) {

        appointmentService.markPaymentSuccess(phone, orderId);
        return "Payment successful";
    }

    // -----------------------
    // PAYMENT FAILURE (TEMP)
    // -----------------------
    @PostMapping("/failed")
    public String paymentFailed(@RequestParam String phone) {

        appointmentService.markPaymentFailed(phone);
        return "Payment failed";
    }
}
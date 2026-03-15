package com.arify.pomi.webhook;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class MetaMessageSender {

    @Value("${meta.phone.number.id}")
    private String phoneNumberId;

    @Value("${meta.access.token}")
    private String accessToken;

    private final RestTemplate restTemplate = new RestTemplate();

    public void sendTextMessage(String to, String message) {

        String url = "https://graph.facebook.com/v21.0/" + phoneNumberId + "/messages";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        Map<String, Object> body = Map.of(
                "messaging_product", "whatsapp",
                "to", to,
                "type", "text",
                "text", Map.of("body", message));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        restTemplate.postForEntity(url, request, String.class);
    }

    public void sendBookingButton(String to, String token) {

        String url = "https://graph.facebook.com/v21.0/" + phoneNumberId + "/messages";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        Map<String, Object> body = Map.of(
                "messaging_product", "whatsapp",
                "to", to,
                "type", "interactive",
                "interactive", Map.of(
                        "type", "cta_url",
                        "body", Map.of(
                                "text", "Click below to book your appointment"),
                        "action", Map.of(
                                "name", "cta_url",
                                "parameters", Map.of(
                                        "display_text", "Book Appointment",
                                        "url", "https://arifysolutions.co.in/book?t=" + token))));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        restTemplate.postForEntity(url, request, String.class);
    }

}
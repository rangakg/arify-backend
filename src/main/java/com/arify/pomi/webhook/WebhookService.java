package com.arify.pomi.webhook;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.arify.pomi.repository.UserRepository;
import com.arify.pomi.repository.AppointmentRepository;
import com.arify.pomi.entity.AppointmentStatus;
import com.arify.pomi.entity.UserEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class WebhookService {

    private static final Logger log = LoggerFactory.getLogger(WebhookService.class);

    private final MetaMessageSender sender;
    private final ObjectMapper mapper = new ObjectMapper();

    private final UserRepository userRepo;
    private final AppointmentRepository apptRepo;

    public WebhookService(
            MetaMessageSender sender,
            UserRepository userRepo,
            AppointmentRepository apptRepo) {

        this.sender = sender;
        this.userRepo = userRepo;
        this.apptRepo = apptRepo;
    }

    public void processIncoming(String payload, String signature) {

        try {

            JsonNode root = mapper.readTree(payload);

            JsonNode messageNode = root
                    .path("entry").get(0)
                    .path("changes").get(0)
                    .path("value")
                    .path("messages").get(0);

            if (messageNode == null)
                return;

            String phone = messageNode.path("from").asText();

            String text = messageNode.path("text")
                    .path("body")
                    .asText()
                    .trim();

            log.info("Message {} -> {}", phone, text);

            var userOpt = userRepo.findById(phone);

            // ----------------------------------
            // NEW USER
            // ----------------------------------

            if (userOpt.isEmpty()) {

                if (text.equalsIgnoreCase("hi")
                        || text.equalsIgnoreCase("book")) {

                    sender.sendTextMessage(phone,
                            "Welcome to Arify.\n\nPlease enter your name.");

                    return;
                }

                UserEntity u = new UserEntity();

                u.setPhone(phone);
                u.setName(text);

                userRepo.save(u);

                sender.sendTextMessage(phone,
                        "Thanks " + text +
                                "\n\nBook here:\n" +
                                "https://arifysolutions.co.in/book?phone=" + phone);

                return;
            }

            // ----------------------------------
            // EXISTING USER
            // ----------------------------------

            boolean hasAppointment = apptRepo.existsByPhoneAndStatus(phone, AppointmentStatus.CREATED);

            if (hasAppointment) {

                sender.sendTextMessage(phone,
                        "You already have an appointment.");

                return;
            }

            sender.sendTextMessage(phone,
                    "Continue booking:\n" +
                            "https://arifysolutions.co.in/book?phone=" + phone);

        } catch (Exception e) {

            log.error("Webhook error", e);
        }

    }
}
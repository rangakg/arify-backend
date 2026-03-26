package com.arify.pomi.webhook;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.arify.pomi.repository.UserRepository;
import com.arify.pomi.repository.AppointmentRepository;
import com.arify.pomi.entity.AppointmentEntity;
import com.arify.pomi.entity.AppointmentStatus;
import com.arify.pomi.entity.SlotEntity;
import com.arify.pomi.entity.UserEntity;

import java.time.ZoneId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.JdbcTemplate;

@Service
public class WebhookService {

    private static final Logger log = LoggerFactory.getLogger(WebhookService.class);

    private final MetaMessageSender sender;
    private final ObjectMapper mapper = new ObjectMapper();

    private final UserRepository userRepo;
    private final AppointmentRepository apptRepo;
    private final JdbcTemplate jdbcTemplate;

    public WebhookService(
            MetaMessageSender sender,
            UserRepository userRepo,
            AppointmentRepository apptRepo,
            JdbcTemplate jdbcTemplate) {

        this.sender = sender;
        this.userRepo = userRepo;
        this.apptRepo = apptRepo;
        this.jdbcTemplate = jdbcTemplate;
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

            String text = messageNode
                    .path("text")
                    .path("body")
                    .asText()
                    .trim();

            log.info("Message {} -> {}", phone, text);

            var userOpt = userRepo.findById(phone);

            // ----------------------------------
            // STEP 1 : USER NOT REGISTERED
            // ----------------------------------

            if (userOpt.isEmpty()) {

                if (text.equalsIgnoreCase("hi") || text.equalsIgnoreCase("book")) {

                    sender.sendTextMessage(
                            phone,
                            "Welcome to Arify.\n\nPlease enter your name.");

                    return;
                }

                // Save new user
                UserEntity u = new UserEntity();
                u.setPhone(phone);
                u.setName(text);

                userRepo.save(u);

                // generate token
                String token = generateToken(phone);

                sender.sendBookingButton(phone, token);

                return;
            }

            // ----------------------------------
            // STEP 2 : EXISTING USER
            // ----------------------------------

            // ----------------------------------
            // STEP 2 : EXISTING USER
            // ----------------------------------

            AppointmentEntity appt = apptRepo.findById(phone).orElse(null);

            if (appt != null && appt.getStatus() == AppointmentStatus.LOCKED) {

                String token = generateToken(phone);

                SlotEntity slot = appt.getSlot();

                // String message = "⏳ You have a pending booking\n\n" +
                ZoneId IST = ZoneId.of("Asia/Kolkata");

                String message = "⏳ You have a pending booking\n\n" +
                        "Doctor: " + slot.getDoctor().getName() + "\n" +
                        "Date: " + slot.getSlotTime()
                                .atZoneSameInstant(IST)
                                .toLocalDate()
                        + "\n" +
                        "Time: " + slot.getSlotTime()
                                .atZoneSameInstant(IST)
                                .toLocalTime()
                        + "\n\n" +
                        "Choose an option:";

                sender.sendTextMessage(phone, message);

                // 🔥 IMPORTANT: Send 2 buttons
                sender.sendTwoButtons(
                        phone,
                        "Continue or change your booking",
                        "Proceed to Payment",
                        "Change Slot",
                        token);

                return;
            }

            // generate token for existing user
            String token = generateToken(phone);

            sender.sendBookingButton(phone, token);
        } catch (Exception e) {

            log.error("Webhook error", e);
        }
    }

    // ----------------------------------
    // TOKEN GENERATION
    // ----------------------------------

    private String generateToken(String phone) {

        // check existing valid token
        var tokens = jdbcTemplate.query(
                """
                        SELECT token
                        FROM booking_tokens
                        WHERE phone = ?
                        AND expires_at > now()
                        LIMIT 1
                        """,
                (rs, rowNum) -> rs.getString("token"),
                phone);

        if (!tokens.isEmpty()) {
            return tokens.get(0);
        }

        // otherwise create new token
        String token = java.util.UUID.randomUUID().toString();

        jdbcTemplate.update(
                """
                        INSERT INTO booking_tokens(token, phone, expires_at)
                        VALUES (?, ?, now() + interval '30 minutes')
                        """,
                token,
                phone);

        return token;
    }
}
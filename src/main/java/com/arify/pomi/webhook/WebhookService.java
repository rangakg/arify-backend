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

                sender.sendTextMessage(
                        phone,
                        "Thanks " + text +
                                "\n\nBook your appointment here:\n" +
                                "https://arifysolutions.co.in/book?t=" + token);

                return;
            }

            // ----------------------------------
            // STEP 2 : EXISTING USER
            // ----------------------------------

            boolean hasAppointment = apptRepo.existsByPhoneAndStatus(phone, AppointmentStatus.CREATED);

            if (hasAppointment) {

                sender.sendTextMessage(
                        phone,
                        "You already have an appointment.");

                return;
            }

            // generate token for existing user
            String token = generateToken(phone);

            sender.sendTextMessage(
                    phone,
                    "Continue booking here:\n" +
                            "https://arifysolutions.co.in/book?t=" + token);

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
package com.arify.pomi.webhook;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class WebhookService {

    private static final Logger log = LoggerFactory.getLogger(WebhookService.class);

    private final MetaMessageSender metaMessageSender;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public WebhookService(MetaMessageSender metaMessageSender) {
        this.metaMessageSender = metaMessageSender;
    }

    public void processIncoming(String payload, String signature) {

        try {

            log.info("Received Webhook Payload: {}", payload);

            JsonNode root = objectMapper.readTree(payload);

            JsonNode messageNode = root
                    .path("entry").get(0)
                    .path("changes").get(0)
                    .path("value")
                    .path("messages").get(0);

            if (messageNode == null)
                return;

            String from = messageNode.path("from").asText();
            String text = messageNode.path("text").path("body").asText();

            log.info("Message from {}: {}", from, text);

            metaMessageSender.sendTextMessage(from, "Hello from Arify 🚀");

        } catch (Exception e) {
            log.error("Error processing webhook", e);
        }
    }
}
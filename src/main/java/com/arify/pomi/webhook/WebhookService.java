package com.arify.pomi.webhook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class WebhookService {

    private static final Logger log = LoggerFactory.getLogger(WebhookService.class);

    public void processIncoming(String payload, String signature) {

        log.info("Received Webhook Payload: {}", payload);

        // TODO:
        // 1. Verify signature
        // 2. Parse JSON
        // 3. Extract phone + message
        // 4. Route to message handler
    }
}
package com.rapidx.accoutservice.client.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class NotificationClient {

    private static final Logger log = LoggerFactory.getLogger(NotificationClient.class);

    public void sendNotification(String recipient, String message) {
        log.info("Sending notification to: {} | Message: {}", recipient, message);
        // Stub implementation - in real life this would call external API (SNS, SMTP, Kafka, etc.)
    }
}

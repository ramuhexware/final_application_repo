package com.rapidx.accoutservice.client.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

@Service
public class EmailNotificationClientServicer {

    private static final Logger log = LoggerFactory.getLogger(EmailNotificationClientServicer.class);

    private final EmailNotificationClientConfig config;
    private final RestTemplate restTemplate;

    public EmailNotificationClientServicer(EmailNotificationClientConfig config) {
        this.config = config;
        this.restTemplate = new RestTemplate();
    }

    public void sendEmailNotification(String recipient, String subject, String body) {
        String url = config.getEmailServiceUrl() + "/api/notifications/email";
        log.info("Sending email notification via email-service: {}", url);
        
        Map<String, String> request = new HashMap<>();
        request.put("recipient", recipient);
        request.put("subject", subject);
        request.put("body", body);
        
        try {
            String response = restTemplate.postForObject(url, request, String.class);
            log.info("Email notification service response: {}", response);
        } catch (Exception e) {
            log.error("Failed to send email notification: {}", e.getMessage());
        }
    }
}

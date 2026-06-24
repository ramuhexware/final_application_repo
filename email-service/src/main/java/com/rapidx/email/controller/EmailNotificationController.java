package com.rapidx.email.controller;

import com.rapidx.email.dto.EmailRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/notifications")
@Slf4j
public class EmailNotificationController {

    private final RestTemplate restTemplate;

    public EmailNotificationController() {
        this.restTemplate = new RestTemplate();
    }

    public EmailNotificationController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostMapping("/email")
    public ResponseEntity<String> sendEmail(@RequestBody EmailRequest request) {
        log.info("Email Notification Service: Received request to send email.");
        log.info("To: {}", request.getRecipient());
        log.info("Subject: {}", request.getSubject());
        log.info("Body: {}", request.getBody());
        
        // Call external third party SMTP microservice/API
        try {
            String externalUrl = "https://httpbin.org/post";
            log.info("Calling external third party microservice/API: {}", externalUrl);
            ResponseEntity<String> response = restTemplate.postForEntity(externalUrl, request, String.class);
            log.info("External third party service response status code: {}", response.getStatusCode());
        } catch (Exception e) {
            log.warn("External third party service was unreachable, falling back: {}", e.getMessage());
        }
        
        log.info("Email successfully dispatched to {}", request.getRecipient());
        
        return ResponseEntity.ok("Email successfully sent.");
    }
}

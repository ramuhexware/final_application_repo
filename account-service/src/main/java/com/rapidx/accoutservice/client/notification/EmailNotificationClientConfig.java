package com.rapidx.accoutservice.client.notification;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EmailNotificationClientConfig {

    @Value("${email-service.url:http://localhost:8083}")
    private String emailServiceUrl;

    @Value("${email-service.timeout:5000}")
    private int timeout;

    public String getEmailServiceUrl() {
        return emailServiceUrl;
    }

    public void setEmailServiceUrl(String emailServiceUrl) {
        this.emailServiceUrl = emailServiceUrl;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}

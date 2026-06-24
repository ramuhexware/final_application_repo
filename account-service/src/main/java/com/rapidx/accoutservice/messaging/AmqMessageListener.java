package com.rapidx.accoutservice.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class AmqMessageListener {

    private static final Logger log = LoggerFactory.getLogger(AmqMessageListener.class);

    private String lastReceivedMessage;

    @JmsListener(destination = "${spring.activemq.queue-name}")
    public void receiveMessage(String message) {
        log.info("Received JMS message from queue: {}", message);
        this.lastReceivedMessage = message;
    }

    public String getLastReceivedMessage() {
        return lastReceivedMessage;
    }

    public void clearLastReceivedMessage() {
        this.lastReceivedMessage = null;
    }
}

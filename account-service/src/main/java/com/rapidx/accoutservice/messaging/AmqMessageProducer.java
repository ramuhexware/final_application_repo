package com.rapidx.accoutservice.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class AmqMessageProducer {

    private static final Logger log = LoggerFactory.getLogger(AmqMessageProducer.class);

    private final JmsTemplate jmsTemplate;
    
    @Value("${spring.activemq.queue-name}")
    private String queueName;

    public AmqMessageProducer(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void publishEvent(String eventType, Long accountId, String accountName) {
        String payload = String.format("{\"eventType\": \"%s\", \"accountId\": %d, \"accountName\": \"%s\"}", 
                eventType, accountId, accountName);
        log.info("Publishing JMS message to queue {}: {}", queueName, payload);
        try {
            jmsTemplate.convertAndSend(queueName, payload);
        } catch (Exception e) {
            log.error("Failed to publish JMS message to queue {}", queueName, e);
        }
    }
}

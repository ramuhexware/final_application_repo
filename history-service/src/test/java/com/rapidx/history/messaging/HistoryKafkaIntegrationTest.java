package com.rapidx.history.messaging;

import com.rapidx.history.entity.HistoryRecord;
import com.rapidx.history.repository.HistoryRecordRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@EnableAutoConfiguration(exclude = {
        DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
})
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
public class HistoryKafkaIntegrationTest {

    @Autowired
    private HistoryKafkaProducer producer;

    @Autowired
    private HistoryKafkaConsumer consumer;

    @MockBean
    private HistoryRecordRepository repository;

    @Test
    void testProducerConsumerLoop() throws Exception {
        // Mock repository save behavior
        HistoryRecord mockSaved = new HistoryRecord(1L, "TEST_EVENT", "{\"data\": \"value\"}", LocalDateTime.now());
        when(repository.save(any(HistoryRecord.class))).thenReturn(mockSaved);

        // Clear consumer state
        consumer.clearLastReceivedRecord();

        // Create a test record
        HistoryRecord record = new HistoryRecord();
        record.setEventType("TEST_EVENT");
        record.setPayload("{\"data\": \"value\"}");
        record.setCreatedAt(LocalDateTime.now());

        // Publish to Kafka
        producer.sendHistoryEvent(record);

        // Wait for consumer to process the event
        int count = 0;
        while (consumer.getLastReceivedRecord() == null && count < 50) {
            Thread.sleep(100);
            count++;
        }

        // Verify consumer received and persisted the record
        HistoryRecord received = consumer.getLastReceivedRecord();
        assertThat(received).isNotNull();
        assertThat(received.getEventType()).isEqualTo("TEST_EVENT");
        assertThat(received.getPayload()).contains("value");
    }
}

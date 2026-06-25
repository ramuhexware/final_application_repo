package com.rapidx.history.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "history_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoryRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(name = "payload", length = 1000)
    private String payload;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}

package com.rapidx.aggregator.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "oim_sync_error", schema = "ba0352")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OimSyncError {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "url", length = 1000)
    private String url;

    @Column(name = "entity_id")
    private String entityId;

    @Column(name = "operation")
    private String operation;

    @Column(name = "error_message", length = 2000)
    private String errorMessage;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}

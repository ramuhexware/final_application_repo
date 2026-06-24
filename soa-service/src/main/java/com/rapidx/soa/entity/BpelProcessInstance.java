package com.rapidx.soa.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bpel_process_instances")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BpelProcessInstance {

    @Id
    @Column(name = "process_id", length = 100)
    private String processId;

    @Column(name = "process_name", nullable = false, length = 100)
    private String processName;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Lob
    @Column(name = "payload")
    private String payload;

    @Lob
    @Column(name = "response")
    private String response;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

package com.akshadip.helios.models;

import com.akshadip.helios.enums.JobStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "jobs")
@Getter
@Setter
public class Job {

    @Id
    private UUID id;

    private String name;

    @Column(name = "cron_expression")
    private String cronExpression;

    //add enum for this
    @Enumerated(EnumType.STRING)
    private JobStatus status;

    @Column(name = "job_type")
    private String jobType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String payload;

    @Column(name = "next_fire_at")
    private LocalDateTime nextFireAt;

    @Column(name = "last_fire_at")
    private LocalDateTime lastFireAt;

    @Column(name = "retry_count")
    private Integer retryCount;

    @Column(name = "max_retries")
    private Integer maxRetries;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}

package com.akshadip.helios.models;

import com.akshadip.helios.enums.JobStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JobTest {

    @Test
    void shouldSetAndGetAllFields() {
        Job job = new Job();
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        job.setId(id);
        job.setName("test-job");
        job.setCronExpression("0 * * * * *");
        job.setStatus(JobStatus.PENDING);
        job.setJobType("HTTP");
        job.setPayload("{\"url\":\"http://example.com\"}");
        job.setNextFireAt(now);
        job.setLastFireAt(now.minusMinutes(1));
        job.setRetryCount(0);
        job.setMaxRetries(3);
        job.setCreatedAt(now);
        job.setUpdatedAt(now);

        assertEquals(id, job.getId());
        assertEquals("test-job", job.getName());
        assertEquals("0 * * * * *", job.getCronExpression());
        assertEquals(JobStatus.PENDING, job.getStatus());
        assertEquals("HTTP", job.getJobType());
        assertEquals("{\"url\":\"http://example.com\"}", job.getPayload());
        assertEquals(now, job.getNextFireAt());
        assertEquals(now.minusMinutes(1), job.getLastFireAt());
        assertEquals(0, job.getRetryCount());
        assertEquals(3, job.getMaxRetries());
        assertEquals(now, job.getCreatedAt());
        assertEquals(now, job.getUpdatedAt());
    }

    @Test
    void defaultValuesShouldBeNull() {
        Job job = new Job();
        assertNull(job.getId());
        assertNull(job.getName());
        assertNull(job.getCronExpression());
        assertNull(job.getStatus());
        assertNull(job.getJobType());
        assertNull(job.getPayload());
        assertNull(job.getNextFireAt());
        assertNull(job.getLastFireAt());
        assertNull(job.getRetryCount());
        assertNull(job.getMaxRetries());
        assertNull(job.getCreatedAt());
        assertNull(job.getUpdatedAt());
    }

    @Test
    void shouldUpdateStatus() {
        Job job = new Job();
        job.setStatus(JobStatus.PENDING);
        assertEquals(JobStatus.PENDING, job.getStatus());

        job.setStatus(JobStatus.RUNNING);
        assertEquals(JobStatus.RUNNING, job.getStatus());

        job.setStatus(JobStatus.COMPLETED);
        assertEquals(JobStatus.COMPLETED, job.getStatus());
    }

    @Test
    void shouldIncrementRetryCount() {
        Job job = new Job();
        job.setRetryCount(0);
        assertEquals(0, job.getRetryCount());

        job.setRetryCount(job.getRetryCount() + 1);
        assertEquals(1, job.getRetryCount());

        job.setRetryCount(job.getRetryCount() + 1);
        assertEquals(2, job.getRetryCount());
    }
}

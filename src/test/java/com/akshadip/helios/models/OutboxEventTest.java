package com.akshadip.helios.models;

import com.akshadip.helios.enums.OutboxStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OutboxEventTest {

    @Test
    void shouldSetAndGetAllFields() {
        OutboxEvent event = new OutboxEvent();
        UUID id = UUID.randomUUID();
        UUID jobId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        event.setId(id);
        event.setJobId(jobId);
        event.setStatus(OutboxStatus.PENDING);
        event.setRetryCount(0);
        event.setCreatedAt(now);
        event.setSentAt(now.plusSeconds(5));

        assertEquals(id, event.getId());
        assertEquals(jobId, event.getJobId());
        assertEquals(OutboxStatus.PENDING, event.getStatus());
        assertEquals(0, event.getRetryCount());
        assertEquals(now, event.getCreatedAt());
        assertEquals(now.plusSeconds(5), event.getSentAt());
    }

    @Test
    void defaultValuesShouldBeNull() {
        OutboxEvent event = new OutboxEvent();
        assertNull(event.getId());
        assertNull(event.getJobId());
        assertNull(event.getStatus());
        assertNull(event.getCreatedAt());
        assertNull(event.getSentAt());
    }

    @Test
    void shouldUpdateStatusToSent() {
        OutboxEvent event = new OutboxEvent();
        event.setStatus(OutboxStatus.PENDING);
        assertEquals(OutboxStatus.PENDING, event.getStatus());

        event.setStatus(OutboxStatus.SENT);
        event.setSentAt(LocalDateTime.now());
        assertEquals(OutboxStatus.SENT, event.getStatus());
        assertNotNull(event.getSentAt());
    }

    @Test
    void shouldIncrementRetryCount() {
        OutboxEvent event = new OutboxEvent();
        event.setRetryCount(0);
        assertEquals(0, event.getRetryCount());

        event.setRetryCount(event.getRetryCount() + 1);
        assertEquals(1, event.getRetryCount());
    }
}

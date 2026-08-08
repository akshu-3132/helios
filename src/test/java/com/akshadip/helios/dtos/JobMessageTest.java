package com.akshadip.helios.dtos;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JobMessageTest {

    @Test
    void shouldCreateWithJobId() {
        UUID jobId = UUID.randomUUID();
        JobMessage message = new JobMessage(jobId);
        assertEquals(jobId, message.getJobId());
    }

    @Test
    void noArgsConstructorShouldCreateEmptyMessage() {
        JobMessage message = new JobMessage();
        assertNull(message.getJobId());
    }

    @Test
    void shouldSetAndGetJobId() {
        JobMessage message = new JobMessage();
        UUID jobId = UUID.randomUUID();
        message.setJobId(jobId);
        assertEquals(jobId, message.getJobId());
    }

    @Test
    void shouldHandleNullJobId() {
        JobMessage message = new JobMessage(null);
        assertNull(message.getJobId());
    }
}

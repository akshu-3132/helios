package com.akshadip.helios.exceptions;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JobNotFoundExceptionTest {

    @Test
    void shouldContainJobIdInMessage() {
        UUID jobId = UUID.randomUUID();
        JobNotFoundException exception = new JobNotFoundException(jobId);
        assertTrue(exception.getMessage().contains(jobId.toString()));
    }

    @Test
    void shouldExtendRuntimeException() {
        UUID jobId = UUID.randomUUID();
        JobNotFoundException exception = new JobNotFoundException(jobId);
        assertInstanceOf(RuntimeException.class, exception);
    }

    @Test
    void messageShouldStartWithExpectedPrefix() {
        UUID jobId = UUID.randomUUID();
        JobNotFoundException exception = new JobNotFoundException(jobId);
        assertTrue(exception.getMessage().startsWith("Job not found with id:"));
    }
}

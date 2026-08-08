package com.akshadip.helios.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JobStatusTest {

    @Test
    void shouldHaveSixValues() {
        assertEquals(6, JobStatus.values().length);
    }

    @Test
    void shouldContainPending() {
        assertNotNull(JobStatus.PENDING);
    }

    @Test
    void shouldContainRunning() {
        assertNotNull(JobStatus.RUNNING);
    }

    @Test
    void shouldContainCompleted() {
        assertNotNull(JobStatus.COMPLETED);
    }

    @Test
    void shouldContainFailed() {
        assertNotNull(JobStatus.FAILED);
    }

    @Test
    void shouldContainDispatched() {
        assertNotNull(JobStatus.DISPATCHED);
    }

    @Test
    void shouldContainPaused() {
        assertNotNull(JobStatus.PAUSED);
    }

    @Test
    void valueOfShouldReturnCorrectEnum() {
        assertEquals(JobStatus.PENDING, JobStatus.valueOf("PENDING"));
        assertEquals(JobStatus.RUNNING, JobStatus.valueOf("RUNNING"));
    }

    @Test
    void invalidValueOfShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> JobStatus.valueOf("INVALID"));
    }
}

package com.akshadip.helios.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OutboxStatusTest {

    @Test
    void shouldHaveTwoValues() {
        assertEquals(2, OutboxStatus.values().length);
    }

    @Test
    void shouldContainPending() {
        assertNotNull(OutboxStatus.PENDING);
    }

    @Test
    void shouldContainSent() {
        assertNotNull(OutboxStatus.SENT);
    }

    @Test
    void valueOfShouldReturnCorrectEnum() {
        assertEquals(OutboxStatus.PENDING, OutboxStatus.valueOf("PENDING"));
        assertEquals(OutboxStatus.SENT, OutboxStatus.valueOf("SENT"));
    }

    @Test
    void invalidValueOfShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> OutboxStatus.valueOf("INVALID"));
    }
}

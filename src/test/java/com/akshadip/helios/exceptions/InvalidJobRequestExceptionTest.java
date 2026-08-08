package com.akshadip.helios.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InvalidJobRequestExceptionTest {

    @Test
    void shouldContainMessage() {
        InvalidJobRequestException exception = new InvalidJobRequestException("Invalid payload");
        assertEquals("Invalid payload", exception.getMessage());
    }

    @Test
    void shouldExtendRuntimeException() {
        InvalidJobRequestException exception = new InvalidJobRequestException("test");
        assertInstanceOf(RuntimeException.class, exception);
    }

    @Test
    void shouldHandleNullMessage() {
        InvalidJobRequestException exception = new InvalidJobRequestException(null);
        assertNull(exception.getMessage());
    }
}

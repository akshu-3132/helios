package com.akshadip.helios.dtos;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JobResponseDtoTest {

    @Test
    void shouldSetAndGetId() {
        JobResponseDto dto = new JobResponseDto();
        dto.setId("550e8400-e29b-41d4-a716-446655440000");
        assertEquals("550e8400-e29b-41d4-a716-446655440000", dto.getId());
    }

    @Test
    void shouldSetAndGetName() {
        JobResponseDto dto = new JobResponseDto();
        dto.setName("test-job");
        assertEquals("test-job", dto.getName());
    }

    @Test
    void shouldSetAndGetJobType() {
        JobResponseDto dto = new JobResponseDto();
        dto.setJobType("HTTP");
        assertEquals("HTTP", dto.getJobType());
    }

    @Test
    void defaultValuesShouldBeNull() {
        JobResponseDto dto = new JobResponseDto();
        assertNull(dto.getId());
        assertNull(dto.getName());
        assertNull(dto.getJobType());
    }
}

package com.akshadip.helios.mappers;

import com.akshadip.helios.dtos.JobResponseDto;
import com.akshadip.helios.enums.JobStatus;
import com.akshadip.helios.models.Job;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JobMapperTest {

    private JobMapper jobMapper;

    @BeforeEach
    void setUp() {
        jobMapper = new JobMapper();
    }

    @Test
    void shouldMapJobToResponseDto() {
        Job job = new Job();
        UUID id = UUID.randomUUID();
        job.setId(id);
        job.setName("test-job");
        job.setJobType("HTTP");
        job.setCronExpression("0 * * * * *");
        job.setStatus(JobStatus.PENDING);

        JobResponseDto response = jobMapper.toJobResponse(job);

        assertEquals(id.toString(), response.getId());
        assertEquals("test-job", response.getName());
        assertEquals("HTTP", response.getJobType());
    }

    @Test
    void shouldHandleNullJobType() {
        Job job = new Job();
        job.setId(UUID.randomUUID());
        job.setName("test-job");
        job.setJobType(null);

        JobResponseDto response = jobMapper.toJobResponse(job);

        assertNull(response.getJobType());
    }

    @Test
    void shouldPreserveIdAsString() {
        Job job = new Job();
        UUID id = UUID.randomUUID();
        job.setId(id);
        job.setName("test-job");
        job.setJobType("CONSOLE");

        JobResponseDto response = jobMapper.toJobResponse(job);

        assertEquals(id.toString(), response.getId());
        assertDoesNotThrow(() -> UUID.fromString(response.getId()));
    }
}

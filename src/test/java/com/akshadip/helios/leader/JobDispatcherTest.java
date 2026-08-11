package com.akshadip.helios.leader;

import com.akshadip.helios.enums.JobStatus;
import com.akshadip.helios.models.Job;
import com.akshadip.helios.services.JobService;
import com.akshadip.helios.services.OutboxEventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobDispatcherTest {

    @Mock
    private OutboxEventService outboxEventService;

    @Mock
    private JobService jobService;

    @InjectMocks
    private JobDispatcher jobDispatcher;

    private Job job;

    @BeforeEach
    void setUp() {
        job = new Job();
        job.setId(UUID.randomUUID());
        job.setName("test-job");
    }

    @Test
    void dispatchJobShouldCreateOutboxEvent() {
        jobDispatcher.dispatchJob(job);

        verify(outboxEventService).createOutboxEvent(job.getId());
    }

    @Test
    void dispatchJobShouldUpdateJobStatusToDispatched() {
        jobDispatcher.dispatchJob(job);

        verify(jobService).updateJobStatus(job.getId(), JobStatus.DISPATCHED);
    }

    @Test
    void dispatchJobShouldCallBothServices() {
        jobDispatcher.dispatchJob(job);

        verify(outboxEventService, times(1)).createOutboxEvent(job.getId());
        verify(jobService, times(1)).updateJobStatus(job.getId(), JobStatus.DISPATCHED);
    }
}

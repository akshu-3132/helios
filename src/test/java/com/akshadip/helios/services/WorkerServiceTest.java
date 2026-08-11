package com.akshadip.helios.services;

import com.akshadip.helios.dtos.JobMessage;
import com.akshadip.helios.enums.JobStatus;
import com.akshadip.helios.models.Job;
import com.akshadip.helios.scheduler.cron.CronCalculator;
import com.akshadip.helios.worker.HttpJobExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkerServiceTest {

    @Mock
    private HttpJobExecutor httpJobExecutor;

    @Mock
    private CronCalculator cronCalculator;

    @Mock
    private JobService jobService;

    @InjectMocks
    private WorkerService workerService;

    private Job job;
    private JobMessage jobMessage;

    @BeforeEach
    void setUp() {
        UUID jobId = UUID.randomUUID();
        job = new Job();
        job.setId(jobId);
        job.setCronExpression("0 * * * * *");
        job.setPayload("{\"url\":\"http://example.com\"}");
        job.setRetryCount(0);
        job.setMaxRetries(3);

        jobMessage = new JobMessage(jobId);
    }

    @Test
    void executeJobShouldUpdateJobOnSuccess() throws Exception {
        when(jobService.getJobById(jobMessage.getJobId())).thenReturn(job);
        LocalDateTime nextFire = LocalDateTime.of(2026, 8, 11, 12, 0, 0);
        when(cronCalculator.getNextFireTime(any(String.class), any(LocalDateTime.class))).thenReturn(nextFire);

        workerService.executeJob(jobMessage);

        verify(httpJobExecutor).executeJob(job.getPayload());
        assertEquals(nextFire, job.getNextFireAt());
        assertNotNull(job.getLastFireAt());
        assertEquals(JobStatus.PENDING, job.getStatus());
        verify(jobService).updateJob(job);
    }

    @Test
    void executeJobShouldIncrementRetryOnFailure() throws Exception {
        when(jobService.getJobById(jobMessage.getJobId())).thenReturn(job);
        doThrow(new RuntimeException("Execution failed")).when(httpJobExecutor).executeJob(any(String.class));

        workerService.executeJob(jobMessage);

        assertEquals(1, job.getRetryCount());
        assertEquals(JobStatus.PENDING, job.getStatus());
        verify(jobService).updateJob(job);
    }

    @Test
    void executeJobShouldMarkAsFailedWhenMaxRetriesReached() throws Exception {
        job.setRetryCount(2);
        job.setMaxRetries(3);
        when(jobService.getJobById(jobMessage.getJobId())).thenReturn(job);
        doThrow(new RuntimeException("Execution failed")).when(httpJobExecutor).executeJob(any(String.class));

        workerService.executeJob(jobMessage);

        assertEquals(3, job.getRetryCount());
        assertEquals(JobStatus.FAILED, job.getStatus());
        verify(jobService).updateJob(job);
    }

    @Test
    void executeJobShouldAlwaysCallUpdateJob() throws Exception {
        when(jobService.getJobById(jobMessage.getJobId())).thenReturn(job);
        doThrow(new RuntimeException("Execution failed")).when(httpJobExecutor).executeJob(any(String.class));

        workerService.executeJob(jobMessage);

        verify(jobService, times(1)).updateJob(job);
    }

    private void assertEquals(Object expected, Object actual) {
        org.junit.jupiter.api.Assertions.assertEquals(expected, actual);
    }

    private void assertNotNull(Object value) {
        org.junit.jupiter.api.Assertions.assertNotNull(value);
    }
}

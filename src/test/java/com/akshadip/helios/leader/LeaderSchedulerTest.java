package com.akshadip.helios.leader;

import com.akshadip.helios.enums.JobStatus;
import com.akshadip.helios.models.Job;
import com.akshadip.helios.repositories.JobRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeaderSchedulerTest {

    @Mock
    private LeaderElectionService leaderElectionService;

    @Mock
    private JobRepository jobRepository;

    @Mock
    private JobDispatcher jobDispatcher;

    @InjectMocks
    private LeaderScheduler leaderScheduler;

    @Test
    void scheduleShouldSkipWhenNotLeader() {
        when(leaderElectionService.isLeader()).thenReturn(false);

        leaderScheduler.schedule();

        verify(jobRepository, never()).findByNextFireAtBeforeAndStatus(any(), any());
    }

    @Test
    void scheduleShouldDispatchPendingJobsWhenLeader() {
        when(leaderElectionService.isLeader()).thenReturn(true);

        Job job1 = new Job();
        job1.setId(UUID.randomUUID());
        Job job2 = new Job();
        job2.setId(UUID.randomUUID());

        when(jobRepository.findByNextFireAtBeforeAndStatus(any(LocalDateTime.class), eq(JobStatus.PENDING)))
                .thenReturn(List.of(job1, job2));

        leaderScheduler.schedule();

        verify(jobDispatcher).dispatchJob(job1);
        verify(jobDispatcher).dispatchJob(job2);
    }

    @Test
    void scheduleShouldNotDispatchWhenNoPendingJobs() {
        when(leaderElectionService.isLeader()).thenReturn(true);
        when(jobRepository.findByNextFireAtBeforeAndStatus(any(LocalDateTime.class), eq(JobStatus.PENDING)))
                .thenReturn(List.of());

        leaderScheduler.schedule();

        verify(jobDispatcher, never()).dispatchJob(any());
    }

    @Test
    void scheduleShouldDispatchSingleJob() {
        when(leaderElectionService.isLeader()).thenReturn(true);

        Job job = new Job();
        job.setId(UUID.randomUUID());

        when(jobRepository.findByNextFireAtBeforeAndStatus(any(LocalDateTime.class), eq(JobStatus.PENDING)))
                .thenReturn(List.of(job));

        leaderScheduler.schedule();

        verify(jobDispatcher, times(1)).dispatchJob(job);
    }
}

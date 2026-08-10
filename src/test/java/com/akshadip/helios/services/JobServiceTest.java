package com.akshadip.helios.services;

import com.akshadip.helios.dtos.JobRequestDto;
import com.akshadip.helios.dtos.JobResponseDto;
import com.akshadip.helios.enums.JobStatus;
import com.akshadip.helios.exceptions.JobNotFoundException;
import com.akshadip.helios.mappers.JobMapper;
import com.akshadip.helios.models.Job;
import com.akshadip.helios.repositories.JobRepository;
import com.akshadip.helios.scheduler.cron.CronCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobServiceTest {

    @Mock
    private JobRepository jobRepository;

    @Mock
    private CronCalculator cronCalculator;

    @Mock
    private JobMapper jobMapper;

    @InjectMocks
    private JobService jobService;

    private JobRequestDto createRequestDto() {
        JobRequestDto dto = new JobRequestDto();
        dto.setName("test-job");
        dto.setCronExpression("0 * * * * *");
        dto.setJobType("HTTP");
        dto.setMaxRetries(3);
        dto.setPayload("{\"url\":\"http://example.com\"}");
        return dto;
    }

    @Test
    void createJobShouldSaveJobWithCorrectValues() {
        JobRequestDto requestDto = createRequestDto();
        LocalDateTime nextFire = LocalDateTime.of(2026, 8, 10, 12, 0, 0);
        when(cronCalculator.getNextFireTime(any(String.class), any(LocalDateTime.class))).thenReturn(nextFire);
        when(jobMapper.toJobResponse(any(Job.class))).thenReturn(new JobResponseDto());

        jobService.createJob(requestDto);

        ArgumentCaptor<Job> captor = ArgumentCaptor.forClass(Job.class);
        verify(jobRepository).save(captor.capture());
        Job saved = captor.getValue();

        assertNotNull(saved.getId());
        assertEquals("test-job", saved.getName());
        assertEquals("0 * * * * *", saved.getCronExpression());
        assertEquals("HTTP", saved.getJobType());
        assertEquals(0, saved.getRetryCount());
        assertEquals(JobStatus.PENDING, saved.getStatus());
        assertEquals(nextFire, saved.getNextFireAt());
    }

    @Test
    void getJobByIdShouldReturnJobWhenFound() {
        UUID id = UUID.randomUUID();
        Job job = new Job();
        job.setId(id);
        when(jobRepository.findById(id)).thenReturn(Optional.of(job));

        Job result = jobService.getJobById(id);

        assertEquals(id, result.getId());
    }

    @Test
    void getJobByIdShouldThrowWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(jobRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(JobNotFoundException.class, () -> jobService.getJobById(id));
    }

    @Test
    void getAllJobsShouldReturnAllJobs() {
        Job job1 = new Job();
        job1.setId(UUID.randomUUID());
        Job job2 = new Job();
        job2.setId(UUID.randomUUID());
        when(jobRepository.findAll()).thenReturn(List.of(job1, job2));

        List<Job> result = jobService.getAllJobs();

        assertEquals(2, result.size());
    }

    @Test
    void deleteJobShouldDeleteWhenFound() {
        UUID id = UUID.randomUUID();
        Job job = new Job();
        job.setId(id);
        when(jobRepository.findById(id)).thenReturn(Optional.of(job));

        jobService.deleteJob(id);

        verify(jobRepository).delete(job);
    }

    @Test
    void deleteJobShouldThrowWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(jobRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(JobNotFoundException.class, () -> jobService.deleteJob(id));
    }

    @Test
    void updateJobShouldSaveJob() {
        Job job = new Job();
        job.setId(UUID.randomUUID());

        jobService.updateJob(job);

        verify(jobRepository).save(job);
    }

    @Test
    void updateJobStatusShouldSetNewStatus() {
        UUID jobId = UUID.randomUUID();
        Job job = new Job();
        job.setId(jobId);
        job.setStatus(JobStatus.PENDING);
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));

        jobService.updateJobStatus(jobId, JobStatus.RUNNING);

        assertEquals(JobStatus.RUNNING, job.getStatus());
    }

    @Test
    void updateJobStatusShouldThrowWhenJobNotFound() {
        UUID jobId = UUID.randomUUID();
        when(jobRepository.findById(jobId)).thenReturn(Optional.empty());

        assertThrows(JobNotFoundException.class, () ->
                jobService.updateJobStatus(jobId, JobStatus.RUNNING));
    }

    @Test
    void createJobsBulkShouldCreateMultipleJobs() {
        JobRequestDto dto1 = createRequestDto();
        JobRequestDto dto2 = createRequestDto();
        dto2.setName("second-job");
        List<JobRequestDto> dtos = List.of(dto1, dto2);

        LocalDateTime nextFire = LocalDateTime.of(2026, 8, 10, 12, 0, 0);
        when(cronCalculator.getNextFireTime(any(String.class), any(LocalDateTime.class))).thenReturn(nextFire);
        when(jobMapper.toJobResponse(any(Job.class))).thenReturn(new JobResponseDto());

        List<JobResponseDto> result = jobService.createJobsBulk(dtos);

        assertEquals(2, result.size());
        verify(jobRepository, times(2)).save(any(Job.class));
    }
}

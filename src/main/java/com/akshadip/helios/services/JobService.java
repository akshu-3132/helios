package com.akshadip.helios.services;


import com.akshadip.helios.dtos.JobRequestDto;
import com.akshadip.helios.dtos.JobResponseDto;
import com.akshadip.helios.enums.JobStatus;
import com.akshadip.helios.mappers.JobMapper;
import com.akshadip.helios.models.Job;
import com.akshadip.helios.repositories.JobRepository;
import com.akshadip.helios.scheduler.cron.CronCalculator;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Getter
@Setter
public class JobService {
    private JobRepository jobRepository;
    private CronCalculator cronCalculator;
    private JobMapper jobMapper;

    JobService(JobRepository jobRepository, CronCalculator cronCalculator, JobMapper jobMapper) {
        this.jobRepository = jobRepository;
        this.cronCalculator = cronCalculator;
        this.jobMapper = jobMapper;
    }

    public JobResponseDto createJob(JobRequestDto jobRequestDto) {
        //create a new job entity
        LocalDateTime now = LocalDateTime.now();
        Job job = new Job();
        job.setId(UUID.randomUUID());
        job.setName(jobRequestDto.getName());
        job.setCronExpression(jobRequestDto.getCronExpression());
        job.setJobType(jobRequestDto.getJobType());
        job.setPayload(jobRequestDto.getPayload());
        job.setMaxRetries(jobRequestDto.getMaxRetries());
        job.setCreatedAt(now);
        job.setUpdatedAt(now);
        job.setRetryCount(0);
        job.setStatus(JobStatus.PENDING);
        job.setNextFireAt(cronCalculator.getNextFireTime(jobRequestDto.getCronExpression(), now));

        jobRepository.save(job);

        return jobMapper.toJobResponse(job);
    }

    public Job getJobById(UUID id) {
        return jobRepository.findById(id).orElseThrow(() -> new RuntimeException("Job not found"));
    }

    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }


    public void deleteJob(UUID id) {
        Job job = jobRepository.findById(id).orElseThrow(() -> new RuntimeException("Job not found"));
        jobRepository.delete(job);
    }


}

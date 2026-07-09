package com.akshadip.helios.services;

import com.akshadip.helios.dtos.JobMessage;
import com.akshadip.helios.enums.JobStatus;
import com.akshadip.helios.models.Job;
import com.akshadip.helios.scheduler.cron.CronCalculator;
import com.akshadip.helios.worker.HttpJobExecutor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class WorkerService {

    private final HttpJobExecutor httpJobExecutor;
    private final CronCalculator cronCalculator;
    private final JobService jobService;

    public WorkerService(HttpJobExecutor httpJobExecutor, CronCalculator cronCalculator, JobService jobService) {
        this.jobService = jobService;
        this.httpJobExecutor = httpJobExecutor;
        this.cronCalculator = cronCalculator;

    }

    @KafkaListener(topics = "jobs", groupId = "worker-group")
    public void executeJob(JobMessage jobMessage) {
        System.out.println("Received job message: " + jobMessage);
        Job job = jobService.getJobById(jobMessage.getJobId());
        try {
            httpJobExecutor.executeJob(job.getPayload());
            LocalDateTime nextFireAt = cronCalculator.getNextFireTime(job.getCronExpression(), LocalDateTime.now());
            job.setNextFireAt(nextFireAt);
            job.setLastFireAt(LocalDateTime.now());
            job.setStatus(JobStatus.PENDING);
        } catch (Exception e) {
            job.setRetryCount(job.getRetryCount() + 1);
            if (job.getRetryCount() >= job.getMaxRetries()) {
                job.setStatus(JobStatus.FAILED);
            } else {
                job.setStatus(JobStatus.PENDING);
            }
        } finally {
            jobService.updateJob(job);
        }

    }
}

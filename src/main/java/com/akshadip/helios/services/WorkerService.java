package com.akshadip.helios.services;

import com.akshadip.helios.models.Job;
import com.akshadip.helios.scheduler.cron.CronCalculator;
import com.akshadip.helios.worker.HttpJobExecutor;
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

    public void executeJob(Job job) {
        httpJobExecutor.executeJob(job.getPayload());
        LocalDateTime nextFireAt = cronCalculator.getNextFireTime(job.getCronExpression(), LocalDateTime.now());
        job.setNextFireAt(nextFireAt);
        jobService.updateJob(job);

    }
}

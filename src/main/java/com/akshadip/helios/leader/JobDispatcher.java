package com.akshadip.helios.leader;

import com.akshadip.helios.dtos.JobMessage;
import com.akshadip.helios.enums.JobStatus;
import com.akshadip.helios.models.Job;

import com.akshadip.helios.services.JobService;
import com.akshadip.helios.services.OutboxEventService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class JobDispatcher {
    private static final Logger log = LoggerFactory.getLogger(JobDispatcher.class);
    private final OutboxEventService outboxEventService;
    private final JobService jobService;

    public JobDispatcher(OutboxEventService outboxEventService, JobService jobService) {
        this.outboxEventService = outboxEventService;
        this.jobService = jobService;
    }

    @Transactional
    public void dispatchJob(Job job) {
        log.info("Dispatching job with ID: {}", job.getId());
        // Create an outbox event
        outboxEventService.createOutboxEvent(job.getId());

        // update the job status
        jobService.updateJobStatus(job.getId(), JobStatus.DISPATCHED);
    }
}

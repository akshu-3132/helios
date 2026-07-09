package com.akshadip.helios.leader;

import com.akshadip.helios.dtos.JobMessage;
import com.akshadip.helios.enums.JobStatus;
import com.akshadip.helios.models.Job;

import com.akshadip.helios.services.JobService;
import com.akshadip.helios.services.OutboxEventService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

@Component
public class JobDispatcher {
    private final OutboxEventService outboxEventService;
    private final JobService jobService;

    public JobDispatcher(OutboxEventService outboxEventService, JobService jobService) {
        this.outboxEventService = outboxEventService;
        this.jobService = jobService;
    }

    @Transactional
    public void dispatchJob(Job job) {
        // Create an outbox event
        outboxEventService.createOutboxEvent(job.getId());

        // update the job status
        jobService.updateJobStatus(job.getId(), JobStatus.DISPATCHED);
    }
}

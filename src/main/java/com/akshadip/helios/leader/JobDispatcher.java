package com.akshadip.helios.leader;

import com.akshadip.helios.models.Job;
import com.akshadip.helios.services.WorkerService;
import org.springframework.stereotype.Component;

@Component
public class JobDispatcher {
    private final WorkerService workerService;

    public JobDispatcher(WorkerService workerService) {
        this.workerService = workerService;
    }

    public void dispatchJob(Job job) {
        workerService.executeJob(job);
    }
}

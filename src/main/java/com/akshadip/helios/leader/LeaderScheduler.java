package com.akshadip.helios.leader;

import com.akshadip.helios.enums.JobStatus;
import com.akshadip.helios.models.Job;
import com.akshadip.helios.repositories.JobRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LeaderScheduler {
    private static final Logger log = LoggerFactory.getLogger(LeaderScheduler.class);
    private final JobRepository jobRepository;
    private final JobDispatcher jobDispatcher;

    public LeaderScheduler(JobRepository jobRepository, JobDispatcher jobDispatcher) {
        this.jobDispatcher = jobDispatcher;
        this.jobRepository = jobRepository;
    }

    @Scheduled(fixedRate = 5000)
    public void schedule() {
        log.info("LeaderScheduler running at {}", java.time.LocalDateTime.now());

        List<Job> jobs = jobRepository.findByNextFireAtBeforeAndStatus(java.time.LocalDateTime.now(), JobStatus.PENDING);

        log.info("Found {} jobs to execute", jobs.size());
        for (Job job : jobs) {
            jobDispatcher.dispatchJob(job);
        }
    }
}

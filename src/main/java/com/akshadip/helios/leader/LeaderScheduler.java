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
    private final LeaderElectionService leaderElectionService;
    private static final Logger log = LoggerFactory.getLogger(LeaderScheduler.class);
    private final JobRepository jobRepository;
    private final JobDispatcher jobDispatcher;

    public LeaderScheduler(JobRepository jobRepository,
                           JobDispatcher jobDispatcher,
                           LeaderElectionService leaderElectionService) {
        this.jobDispatcher = jobDispatcher;
        this.jobRepository = jobRepository;
        this.leaderElectionService = leaderElectionService;
    }

    @Scheduled(fixedRate = 5000)
    public void schedule() {
        if(!leaderElectionService.isLeader()) {
            log.info("Not the leader");
            return;
        }
        List<Job> jobs = jobRepository.findByNextFireAtBeforeAndStatus(java.time.LocalDateTime.now(), JobStatus.PENDING);
        log.info("Found {} jobs to dispatch", jobs.size());
        for (Job job : jobs) {
            jobDispatcher.dispatchJob(job);
        }
    }
}

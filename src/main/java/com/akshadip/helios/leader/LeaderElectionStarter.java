package com.akshadip.helios.leader;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class LeaderElectionStarter {
    private final LeaderElectionService leaderElectionService;
    public LeaderElectionStarter(LeaderElectionService leaderElectionService) {
        this.leaderElectionService = leaderElectionService;
    }
    @EventListener(ApplicationReadyEvent.class)
    public void startLeaderElection() {
        leaderElectionService.electLeader();
    }
}

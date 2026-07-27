package com.akshadip.helios.leader;

public interface LeaderElectionService {
    boolean isLeader();
    void electLeader();
}

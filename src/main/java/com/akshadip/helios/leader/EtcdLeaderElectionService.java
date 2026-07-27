package com.akshadip.helios.leader;


import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.Election;
import io.etcd.jetcd.Lease;
import io.etcd.jetcd.api.CampaignResponse;
import io.etcd.jetcd.lease.LeaseKeepAliveResponse;
import io.grpc.stub.StreamObserver;
import org.springdoc.core.service.GenericResponseService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

@Service
public class EtcdLeaderElectionService implements LeaderElectionService {
    private Client client;
    public EtcdLeaderElectionService(Client client) {
        this.client = client;
    }

    @Value("${helios.instance.id}")
    private String instanceId;
    private volatile boolean leader = false;

    @Override
    public boolean isLeader() {
        return leader;
    }

    @Override
    public void electLeader() {
        try {
            Lease leaseClient = client.getLeaseClient();
            long leaseId = leaseClient
                    .grant(10)
                    .get()
                    .getID();
            System.out.println("Lease granted with ID: " + leaseId);

            leaseClient.keepAlive(leaseId, new StreamObserver<LeaseKeepAliveResponse>() {
                @Override
                public void onNext(LeaseKeepAliveResponse value) {
                    System.out.println("Lease renewed with ID: " + value.getID() + ", TTL: " + value.getTTL());
                    if(isLeader()) {
                        System.out.println("Leader is :" + instanceId);
                    }
                }

                @Override
                public void onError(Throwable t) {
                    System.err.println("Lease renewal failed: " + t.getMessage());
                }

                @Override
                public void onCompleted() {
                    System.out.println("Lease renewal completed");
                }
            });

            Election electionClient = client.getElectionClient();
            ByteSequence electionName = ByteSequence.from("/helios/leader", StandardCharsets.UTF_8);
            ByteSequence candidateValue = ByteSequence.from(instanceId, StandardCharsets.UTF_8);

            CompletableFuture<Void> future = electionClient
                    .campaign(electionName, leaseId, candidateValue)
                    .thenAccept(response -> {
                        System.out.println("Successfully elected as leader with instance ID: " + instanceId);
                        leader = true;
                    });
        } catch (Exception e) {
            throw new RuntimeException("Failed to elect leader", e);
        }
    }

}

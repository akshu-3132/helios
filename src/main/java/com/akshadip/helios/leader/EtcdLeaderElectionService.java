package com.akshadip.helios.leader;


import com.akshadip.helios.services.KafkaListenerService;
import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.Election;
import io.etcd.jetcd.Lease;
import io.etcd.jetcd.api.CampaignResponse;
import io.etcd.jetcd.lease.LeaseKeepAliveResponse;
import io.grpc.stub.StreamObserver;
import org.springdoc.core.service.GenericResponseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

@Service
public class EtcdLeaderElectionService implements LeaderElectionService {
    private static final Logger log = LoggerFactory.getLogger(EtcdLeaderElectionService.class);
    private Client client;
    private final KafkaListenerService kafkaListenerService;
    public EtcdLeaderElectionService(Client client,KafkaListenerService kafkaListenerService) {
        this.kafkaListenerService = kafkaListenerService;
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
            log.info("Lease granted with ID: {}", leaseId);

            leaseClient.keepAlive(leaseId, new StreamObserver<LeaseKeepAliveResponse>() {
                @Override
                public void onNext(LeaseKeepAliveResponse value) {
                    log.info("Lease renewed with ID: {}, TTL: {}", value.getID(), value.getTTL());
                    if(isLeader()) {
                        log.info("Leader is: {}", instanceId);
                    }
                }

                @Override
                public void onError(Throwable t) {
                    log.error("Lease renewal failed: {}", t.getMessage(), t);
                    kafkaListenerService.startWorker();
                }

                @Override
                public void onCompleted() {
                    log.info("Lease renewal completed");
                    kafkaListenerService.startWorker();
                }
            });

            Election electionClient = client.getElectionClient();
            ByteSequence electionName = ByteSequence.from("/helios/leader", StandardCharsets.UTF_8);
            ByteSequence candidateValue = ByteSequence.from(instanceId, StandardCharsets.UTF_8);

            CompletableFuture<Void> future = electionClient
                    .campaign(electionName, leaseId, candidateValue)
                    .thenAccept(response -> {
                        log.info("Successfully elected as leader with instance ID: {}", instanceId);
                        leader = true;
                        kafkaListenerService.stopWorker();
                    });
        } catch (Exception e) {
            throw new RuntimeException("Failed to elect leader", e);
        }
    }

}

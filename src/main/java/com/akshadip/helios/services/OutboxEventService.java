package com.akshadip.helios.services;

import com.akshadip.helios.enums.OutboxStatus;
import com.akshadip.helios.models.OutboxEvent;
import com.akshadip.helios.repositories.OutboxEventRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class OutboxEventService {
    private final OutboxEventRepository outboxEventRepository;

    public OutboxEventService(OutboxEventRepository outboxEventRepository) {
        this.outboxEventRepository = outboxEventRepository;
    }

    public void createOutboxEvent(UUID jobId) {
        OutboxEvent outboxEvent = new OutboxEvent();
        outboxEvent.setId(UUID.randomUUID());
        outboxEvent.setJobId(jobId);
        outboxEvent.setRetryCount(0);
        outboxEvent.setStatus(OutboxStatus.PENDING);
        outboxEventRepository.save(outboxEvent);
    }

    public List<OutboxEvent> getPendingOutboxEvents(int maxRetryCount) {
        return outboxEventRepository.findByStatusAndRetryCountLessThan(OutboxStatus.PENDING, maxRetryCount);
    }

    @Transactional
    public void updateOutboxEventStatus(UUID outboxEventId, OutboxStatus status) {
        OutboxEvent outboxEvent = outboxEventRepository.findById(outboxEventId)
                .orElseThrow(() -> new RuntimeException("Outbox event not found"));
        outboxEvent.setStatus(status);
        if (status == OutboxStatus.SENT) {
            outboxEvent.setSentAt(LocalDateTime.now());
        }
    }

    @Transactional
    public void incrementRetryCount(UUID outboxEventId){
        OutboxEvent outboxEvent = outboxEventRepository.findById(outboxEventId)
                .orElseThrow(() -> new RuntimeException("Outbox event not found"));
        outboxEvent.setRetryCount(outboxEvent.getRetryCount() + 1);
    }

}

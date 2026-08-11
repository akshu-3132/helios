package com.akshadip.helios.leader;

import com.akshadip.helios.enums.OutboxStatus;
import com.akshadip.helios.models.OutboxEvent;
import com.akshadip.helios.services.OutboxEventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OutboxSchedulerTest {

    @Mock
    private LeaderElectionService leaderElectionService;

    @Mock
    private OutboxEventService outboxEventService;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private OutboxScheduler outboxScheduler;

    @Test
    void processOutboxEventsShouldSkipWhenNotLeader() {
        when(leaderElectionService.isLeader()).thenReturn(false);

        outboxScheduler.processOutboxEvents();

        verify(outboxEventService, never()).getPendingOutboxEvents(anyInt());
    }

    @Test
    void processOutboxEventsShouldProcessWhenLeader() {
        when(leaderElectionService.isLeader()).thenReturn(true);

        OutboxEvent event = new OutboxEvent();
        event.setId(UUID.randomUUID());
        event.setJobId(UUID.randomUUID());

        when(outboxEventService.getPendingOutboxEvents(100)).thenReturn(List.of(event));

        SendResult<String, Object> sendResult = mock(SendResult.class);
        CompletableFuture<SendResult<String, Object>> future = CompletableFuture.completedFuture(sendResult);
        when(kafkaTemplate.send(any(String.class), any(String.class), any())).thenReturn(future);

        outboxScheduler.processOutboxEvents();

        verify(outboxEventService).updateOutboxEventStatus(event.getId(), OutboxStatus.SENT);
    }

    @Test
    void processOutboxEventsShouldIncrementRetryOnFailure() {
        when(leaderElectionService.isLeader()).thenReturn(true);

        OutboxEvent event = new OutboxEvent();
        event.setId(UUID.randomUUID());
        event.setJobId(UUID.randomUUID());

        when(outboxEventService.getPendingOutboxEvents(100)).thenReturn(List.of(event));
        when(kafkaTemplate.send(any(String.class), any(String.class), any()))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("Send failed")));

        outboxScheduler.processOutboxEvents();

        verify(outboxEventService).incrementRetryCount(event.getId());
        verify(outboxEventService, never()).updateOutboxEventStatus(any(), any());
    }

    @Test
    void processOutboxEventsShouldHandleEmptyList() {
        when(leaderElectionService.isLeader()).thenReturn(true);
        when(outboxEventService.getPendingOutboxEvents(100)).thenReturn(List.of());

        outboxScheduler.processOutboxEvents();

        verify(kafkaTemplate, never()).send(any(), any(), any());
    }
}

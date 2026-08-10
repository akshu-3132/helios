package com.akshadip.helios.services;

import com.akshadip.helios.enums.OutboxStatus;
import com.akshadip.helios.models.OutboxEvent;
import com.akshadip.helios.repositories.OutboxEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OutboxEventServiceTest {

    @Mock
    private OutboxEventRepository outboxEventRepository;

    @InjectMocks
    private OutboxEventService outboxEventService;

    private UUID jobId;
    private UUID outboxEventId;

    @BeforeEach
    void setUp() {
        jobId = UUID.randomUUID();
        outboxEventId = UUID.randomUUID();
    }

    @Test
    void createOutboxEventShouldSaveWithCorrectValues() {
        ArgumentCaptor<OutboxEvent> captor = ArgumentCaptor.forClass(OutboxEvent.class);

        outboxEventService.createOutboxEvent(jobId);

        verify(outboxEventRepository).save(captor.capture());
        OutboxEvent saved = captor.getValue();
        assertNotNull(saved.getId());
        assertEquals(jobId, saved.getJobId());
        assertEquals(0, saved.getRetryCount());
        assertEquals(OutboxStatus.PENDING, saved.getStatus());
    }

    @Test
    void getPendingOutboxEventsShouldReturnMatchingEvents() {
        OutboxEvent event1 = new OutboxEvent();
        event1.setId(UUID.randomUUID());
        event1.setStatus(OutboxStatus.PENDING);
        event1.setRetryCount(0);

        OutboxEvent event2 = new OutboxEvent();
        event2.setId(UUID.randomUUID());
        event2.setStatus(OutboxStatus.PENDING);
        event2.setRetryCount(2);

        when(outboxEventRepository.findByStatusAndRetryCountLessThan(OutboxStatus.PENDING, 5))
                .thenReturn(List.of(event1, event2));

        List<OutboxEvent> result = outboxEventService.getPendingOutboxEvents(5);

        assertEquals(2, result.size());
        verify(outboxEventRepository).findByStatusAndRetryCountLessThan(OutboxStatus.PENDING, 5);
    }

    @Test
    void updateOutboxEventStatusShouldSetSentAtWhenSent() {
        OutboxEvent event = new OutboxEvent();
        event.setId(outboxEventId);
        event.setStatus(OutboxStatus.PENDING);

        when(outboxEventRepository.findById(outboxEventId)).thenReturn(Optional.of(event));

        outboxEventService.updateOutboxEventStatus(outboxEventId, OutboxStatus.SENT);

        assertEquals(OutboxStatus.SENT, event.getStatus());
        assertNotNull(event.getSentAt());
    }

    @Test
    void updateOutboxEventStatusShouldNotSetSentAtWhenPending() {
        OutboxEvent event = new OutboxEvent();
        event.setId(outboxEventId);
        event.setStatus(OutboxStatus.SENT);

        when(outboxEventRepository.findById(outboxEventId)).thenReturn(Optional.of(event));

        outboxEventService.updateOutboxEventStatus(outboxEventId, OutboxStatus.PENDING);

        assertEquals(OutboxStatus.PENDING, event.getStatus());
        assertNull(event.getSentAt());
    }

    @Test
    void updateOutboxEventStatusShouldThrowWhenNotFound() {
        when(outboxEventRepository.findById(outboxEventId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                outboxEventService.updateOutboxEventStatus(outboxEventId, OutboxStatus.SENT));
    }

    @Test
    void incrementRetryCountShouldIncreaseByOne() {
        OutboxEvent event = new OutboxEvent();
        event.setId(outboxEventId);
        event.setRetryCount(2);

        when(outboxEventRepository.findById(outboxEventId)).thenReturn(Optional.of(event));

        outboxEventService.incrementRetryCount(outboxEventId);

        assertEquals(3, event.getRetryCount());
    }

    @Test
    void incrementRetryCountShouldThrowWhenNotFound() {
        when(outboxEventRepository.findById(outboxEventId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                outboxEventService.incrementRetryCount(outboxEventId));
    }
}

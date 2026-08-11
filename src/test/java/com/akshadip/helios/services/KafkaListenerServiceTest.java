package com.akshadip.helios.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.MessageListenerContainer;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaListenerServiceTest {

    @Mock
    private KafkaListenerEndpointRegistry registry;

    @InjectMocks
    private KafkaListenerService kafkaListenerService;

    private MessageListenerContainer container;

    @BeforeEach
    void setUp() {
        container = mock(MessageListenerContainer.class);
    }

    @Test
    void startWorkerShouldStartContainerWhenNotRunning() {
        when(registry.getListenerContainer("worker-listener")).thenReturn(container);
        when(container.isRunning()).thenReturn(false);

        kafkaListenerService.startWorker();

        verify(container).start();
    }

    @Test
    void startWorkerShouldNotStartContainerWhenAlreadyRunning() {
        when(registry.getListenerContainer("worker-listener")).thenReturn(container);
        when(container.isRunning()).thenReturn(true);

        kafkaListenerService.startWorker();

        verify(container, never()).start();
    }

    @Test
    void startWorkerShouldNotStartWhenContainerIsNull() {
        when(registry.getListenerContainer("worker-listener")).thenReturn(null);

        kafkaListenerService.startWorker();

        verifyNoInteractions(container);
    }

    @Test
    void stopWorkerShouldStopContainerWhenRunning() {
        when(registry.getListenerContainer("worker-listener")).thenReturn(container);
        when(container.isRunning()).thenReturn(true);

        kafkaListenerService.stopWorker();

        verify(container).stop();
    }

    @Test
    void stopWorkerShouldNotStopContainerWhenNotRunning() {
        when(registry.getListenerContainer("worker-listener")).thenReturn(container);
        when(container.isRunning()).thenReturn(false);

        kafkaListenerService.stopWorker();

        verify(container, never()).stop();
    }

    @Test
    void stopWorkerShouldNotStopWhenContainerIsNull() {
        when(registry.getListenerContainer("worker-listener")).thenReturn(null);

        kafkaListenerService.stopWorker();

        verifyNoInteractions(container);
    }
}

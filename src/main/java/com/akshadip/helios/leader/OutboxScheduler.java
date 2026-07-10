package com.akshadip.helios.leader;

import com.akshadip.helios.dtos.JobMessage;
import com.akshadip.helios.enums.OutboxStatus;
import com.akshadip.helios.models.OutboxEvent;
import com.akshadip.helios.services.OutboxEventService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConditionalOnProperty(
        name="helios.role",
        havingValue="leader"
)
public class OutboxScheduler {
    private final OutboxEventService outboxEventService;
    private final KafkaTemplate<String, JobMessage> kafkaTemplate;

    public OutboxScheduler(OutboxEventService outboxEventService, KafkaTemplate<String, JobMessage> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.outboxEventService = outboxEventService;
    }

    @Scheduled(fixedRate = 20000)
    public void processOutboxEvents() {
        List<OutboxEvent> events = outboxEventService.getPendingOutboxEvents(100);
        for (OutboxEvent event : events) {
            try {
                JobMessage jobMessage = new JobMessage(event.getJobId());
                kafkaTemplate.send("jobs", jobMessage.getJobId().toString(), jobMessage).get();
                outboxEventService.updateOutboxEventStatus(event.getId(), OutboxStatus.SENT);
            } catch (Exception e) {
                outboxEventService.incrementRetryCount(event.getId());
            }
        }
    }
}

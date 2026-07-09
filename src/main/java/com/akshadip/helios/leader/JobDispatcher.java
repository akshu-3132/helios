package com.akshadip.helios.leader;

import com.akshadip.helios.dtos.JobMessage;
import com.akshadip.helios.models.Job;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

@Component
public class JobDispatcher {
    private final KafkaTemplate<String, JobMessage> kafkaTemplate;

    public JobDispatcher(KafkaTemplate<String, JobMessage> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void dispatchJob(Job job) {
        JobMessage jobMessage = new JobMessage(job.getId());
        System.out.println("Dispatching job: " + jobMessage.getJobId());
        kafkaTemplate.send("jobs",jobMessage.getJobId().toString(),jobMessage);
    }
}

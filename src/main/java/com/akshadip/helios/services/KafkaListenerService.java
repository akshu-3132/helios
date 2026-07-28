package com.akshadip.helios.services;

import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.stereotype.Service;

@Service
public class KafkaListenerService {
    private KafkaListenerEndpointRegistry registry;
    public KafkaListenerService(KafkaListenerEndpointRegistry registry) {
        this.registry = registry;
    }

    public void startWorker(){
        MessageListenerContainer container = registry.getListenerContainer("worker-listener");
        if(container != null && !container.isRunning()) {
            container.start();
        }
    }

    public void stopWorker(){
        MessageListenerContainer container = registry.getListenerContainer("worker-listener");
        if(container != null && container.isRunning()) {
            container.stop();
        }
    }


}

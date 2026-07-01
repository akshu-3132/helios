package com.akshadip.helios.worker;

import org.springframework.stereotype.Component;


@Component
public class ConsoleJobExecutor implements JobExecutor {
    @Override
    public void executeJob(String payload) {
        System.out.println("Executing job with payload: " + payload);
    }
}

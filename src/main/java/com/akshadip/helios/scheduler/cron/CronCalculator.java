package com.akshadip.helios.scheduler.cron;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

public interface CronCalculator {
    public LocalDateTime getNextFireTime(String cronExpression, LocalDateTime fromTime);
}

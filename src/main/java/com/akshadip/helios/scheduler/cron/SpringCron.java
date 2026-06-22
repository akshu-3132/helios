package com.akshadip.helios.scheduler.cron;

import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class SpringCron implements CronCalculator {
        @Override
        public LocalDateTime getNextFireTime(String cronExpression,LocalDateTime fromTime) {
            CronExpression cron = CronExpression.parse(cronExpression);
            return cron.next(fromTime);
        }
}

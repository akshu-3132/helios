package com.akshadip.helios.scheduler.cron;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SpringCronTest {

    private SpringCron springCron;

    @BeforeEach
    void setUp() {
        springCron = new SpringCron();
    }

    @Test
    void everyMinuteCronShouldReturnNextMinute() {
        LocalDateTime from = LocalDateTime.of(2026, 8, 10, 10, 30, 0);
        LocalDateTime next = springCron.getNextFireTime("0 * * * * *", from);
        assertEquals(LocalDateTime.of(2026, 8, 10, 10, 31, 0), next);
    }

    @Test
    void everyHourCronShouldReturnNextHour() {
        LocalDateTime from = LocalDateTime.of(2026, 8, 10, 10, 30, 0);
        LocalDateTime next = springCron.getNextFireTime("0 0 * * * *", from);
        assertEquals(LocalDateTime.of(2026, 8, 10, 11, 0, 0), next);
    }

    @Test
    void dailyAtMidnightShouldReturnNextDay() {
        LocalDateTime from = LocalDateTime.of(2026, 8, 10, 15, 30, 0);
        LocalDateTime next = springCron.getNextFireTime("0 0 0 * * *", from);
        assertEquals(LocalDateTime.of(2026, 8, 11, 0, 0, 0), next);
    }

    @Test
    void everyFiveMinutesShouldReturnCorrectTime() {
        LocalDateTime from = LocalDateTime.of(2026, 8, 10, 10, 32, 0);
        LocalDateTime next = springCron.getNextFireTime("0 */5 * * * *", from);
        assertEquals(LocalDateTime.of(2026, 8, 10, 10, 35, 0), next);
    }

    @Test
    void invalidCronExpressionShouldThrowException() {
        LocalDateTime from = LocalDateTime.of(2026, 8, 10, 10, 30, 0);
        assertThrows(IllegalArgumentException.class, () ->
                springCron.getNextFireTime("invalid cron", from));
    }
}

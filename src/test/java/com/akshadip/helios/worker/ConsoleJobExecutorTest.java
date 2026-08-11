package com.akshadip.helios.worker;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class ConsoleJobExecutorTest {

    @Test
    void executeJobShouldPrintPayloadToConsole() {
        ConsoleJobExecutor executor = new ConsoleJobExecutor();
        String payload = "{\"url\":\"http://example.com\"}";

        PrintStream originalOut = System.out;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        try {
            executor.executeJob(payload);
            String output = outputStream.toString();
            assertTrue(output.contains(payload));
            assertTrue(output.contains("Executing job with payload:"));
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void executeJobShouldNotThrowException() {
        ConsoleJobExecutor executor = new ConsoleJobExecutor();
        assertDoesNotThrow(() -> executor.executeJob("test-payload"));
    }

    @Test
    void executeJobShouldHandleEmptyPayload() {
        ConsoleJobExecutor executor = new ConsoleJobExecutor();
        PrintStream originalOut = System.out;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        try {
            executor.executeJob("");
            String output = outputStream.toString();
            assertTrue(output.contains("Executing job with payload: "));
        } finally {
            System.setOut(originalOut);
        }
    }
}

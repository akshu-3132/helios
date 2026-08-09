package com.akshadip.helios.controllers;

import com.akshadip.helios.exceptions.InvalidJobRequestException;
import com.akshadip.helios.exceptions.JobNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void handleJobNotFoundShouldReturn404() {
        UUID jobId = UUID.randomUUID();
        Map<String, String> result = handler.handleJobNotFound(new JobNotFoundException(jobId));
        assertEquals("Not found", result.get("error"));
        assertTrue(result.get("message").contains(jobId.toString()));
    }

    @Test
    void handleInvalidJobRequestShouldReturn400() {
        Map<String, String> result = handler.handleInvalidJobRequest(
                new InvalidJobRequestException("Invalid payload"));
        assertEquals("Bad request", result.get("error"));
        assertEquals("Invalid payload", result.get("message"));
    }

    @Test
    void handleIllegalArgumentShouldReturn400() {
        Map<String, String> result = handler.handleIllegalArgument(
                new IllegalArgumentException("Bad argument"));
        assertEquals("Bad request", result.get("error"));
        assertEquals("Bad argument", result.get("message"));
    }

    @Test
    void handleRuntimeExceptionShouldReturn500() {
        Map<String, String> result = handler.handleRuntimeException(
                new RuntimeException("Something went wrong"));
        assertEquals("Internal server error", result.get("error"));
        assertEquals("An unexpected error occurred", result.get("message"));
    }

    @Test
    void handleExceptionShouldReturn500() {
        Map<String, String> result = handler.handleException(
                new Exception("Something went wrong"));
        assertEquals("Internal server error", result.get("error"));
        assertEquals("An unexpected error occurred", result.get("message"));
    }
}

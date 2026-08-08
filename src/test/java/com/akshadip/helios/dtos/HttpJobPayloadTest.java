package com.akshadip.helios.dtos;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class HttpJobPayloadTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void validPayloadShouldHaveNoViolations() {
        HttpJobPayload payload = new HttpJobPayload();
        payload.setUrl("http://example.com/api");
        Set<ConstraintViolation<HttpJobPayload>> violations = validator.validate(payload);
        assertTrue(violations.isEmpty());
    }

    @Test
    void blankUrlShouldFailValidation() {
        HttpJobPayload payload = new HttpJobPayload();
        payload.setUrl("");
        Set<ConstraintViolation<HttpJobPayload>> violations = validator.validate(payload);
        assertFalse(violations.isEmpty());
    }

    @Test
    void nullUrlShouldFailValidation() {
        HttpJobPayload payload = new HttpJobPayload();
        payload.setUrl(null);
        Set<ConstraintViolation<HttpJobPayload>> violations = validator.validate(payload);
        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldSetAndGetHeaders() {
        HttpJobPayload payload = new HttpJobPayload();
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer token123");
        headers.put("Content-Type", "application/json");
        payload.setHeaders(headers);
        assertEquals(2, payload.getHeaders().size());
        assertEquals("Bearer token123", payload.getHeaders().get("Authorization"));
    }

    @Test
    void shouldSetAndGetBody() {
        HttpJobPayload payload = new HttpJobPayload();
        payload.setBody("{\"key\":\"value\"}");
        assertEquals("{\"key\":\"value\"}", payload.getBody());
    }

    @Test
    void shouldSetGetMethod() {
        HttpJobPayload payload = new HttpJobPayload();
        payload.setMethod("POST");
        assertEquals("POST", payload.getMethod());
    }
}

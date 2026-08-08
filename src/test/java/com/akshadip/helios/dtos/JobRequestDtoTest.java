package com.akshadip.helios.dtos;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class JobRequestDtoTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    private JobRequestDto createValidDto() {
        JobRequestDto dto = new JobRequestDto();
        dto.setName("test-job");
        dto.setCronExpression("0 * * * * *");
        dto.setJobType("HTTP");
        dto.setMaxRetries(3);
        return dto;
    }

    @Test
    void validDtoShouldHaveNoViolations() {
        JobRequestDto dto = createValidDto();
        Set<ConstraintViolation<JobRequestDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void blankNameShouldFailValidation() {
        JobRequestDto dto = createValidDto();
        dto.setName("");
        Set<ConstraintViolation<JobRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
    }

    @Test
    void nullNameShouldFailValidation() {
        JobRequestDto dto = createValidDto();
        dto.setName(null);
        Set<ConstraintViolation<JobRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    void blankCronExpressionShouldFailValidation() {
        JobRequestDto dto = createValidDto();
        dto.setCronExpression("");
        Set<ConstraintViolation<JobRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("cronExpression")));
    }

    @Test
    void blankJobTypeShouldFailValidation() {
        JobRequestDto dto = createValidDto();
        dto.setJobType("");
        Set<ConstraintViolation<JobRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("jobType")));
    }

    @Test
    void nullMaxRetriesShouldFailValidation() {
        JobRequestDto dto = createValidDto();
        dto.setMaxRetries(null);
        Set<ConstraintViolation<JobRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("maxRetries")));
    }

    @Test
    void negativeMaxRetriesShouldFailValidation() {
        JobRequestDto dto = createValidDto();
        dto.setMaxRetries(-1);
        Set<ConstraintViolation<JobRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    void zeroMaxRetriesShouldPassValidation() {
        JobRequestDto dto = createValidDto();
        dto.setMaxRetries(0);
        Set<ConstraintViolation<JobRequestDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void payloadCanBeNullOrString() {
        JobRequestDto dto = createValidDto();
        dto.setPayload(null);
        Set<ConstraintViolation<JobRequestDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());

        dto.setPayload("{\"url\":\"http://example.com\"}");
        violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }
}

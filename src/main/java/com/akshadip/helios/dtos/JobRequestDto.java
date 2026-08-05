package com.akshadip.helios.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobRequestDto {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Cron expression is required")
    private String cronExpression;

    @NotBlank(message = "Job type is required")
    private String jobType;

    private String payload;

    @NotNull(message = "Max retries is required")
    @Min(value = 0, message = "Max retries must be non-negative")
    private Integer maxRetries;
}

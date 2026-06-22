package com.akshadip.helios.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobRequestDto {
    private String name;
    private String cronExpression;
    private String jobType;
    private String payload;
    private Integer maxRetries;
}

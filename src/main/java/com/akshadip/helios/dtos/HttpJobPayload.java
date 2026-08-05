package com.akshadip.helios.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class HttpJobPayload {
    @NotBlank(message = "URL is required")
    private String url;
    private String method;
    private Object body;
    private Map<String, String> headers;
}

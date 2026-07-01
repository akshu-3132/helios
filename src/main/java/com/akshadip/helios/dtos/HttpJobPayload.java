package com.akshadip.helios.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class HttpJobPayload {
    private String url;
    private String method;
    private Object body;
    private Map<String, String> headers;
}

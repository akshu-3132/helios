package com.akshadip.helios.worker;

import com.akshadip.helios.dtos.HttpJobPayload;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;


@Component
public class HttpJobExecutor implements JobExecutor {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public HttpJobExecutor(RestClient restClient, ObjectMapper objectMapper) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public void executeJob(String payload) {
        try {
            HttpJobPayload httpJobPayload = objectMapper.readValue(payload, HttpJobPayload.class);
            String response = restClient.get()
                    .uri(httpJobPayload.getUrl())
                    .headers(headers -> httpJobPayload.getHeaders().forEach(headers::add))
                    .retrieve()
                    .body(String.class);
            System.out.println("HTTP Job executed successfully. Response: " + response);

        } catch (Exception e) {
            //TODO - Handle exception, log error, etc.
        }
    }

}

package com.akshadip.helios.exceptions;

import java.util.UUID;

public class JobNotFoundException extends RuntimeException {
    public JobNotFoundException(UUID jobId) {
        super("Job not found with id: " + jobId);
    }
}

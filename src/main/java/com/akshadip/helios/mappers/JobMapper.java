package com.akshadip.helios.mappers;

import com.akshadip.helios.dtos.JobResponseDto;
import com.akshadip.helios.models.Job;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class JobMapper {
    public JobResponseDto toJobResponse(Job job) {
        JobResponseDto jobResponse = new JobResponseDto();
        jobResponse.setId(job.getId().toString());
        jobResponse.setName(job.getName());
        jobResponse.setJobType(job.getJobType());
        return jobResponse;
    }
}

package com.akshadip.helios.controllers;

import com.akshadip.helios.dtos.JobRequestDto;
import com.akshadip.helios.dtos.JobResponseDto;
import com.akshadip.helios.models.Job;
import com.akshadip.helios.services.JobService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class JobController {
    JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping("/jobs/create")
    public JobResponseDto createJob(@RequestBody JobRequestDto jobRequestDto) {
        return jobService.createJob(jobRequestDto);
    }

    @GetMapping("/jobs/{id}")
    public Job getJobById(@PathVariable String id) {
        return jobService.getJobById(UUID.fromString(id));
    }

    @GetMapping("/jobs")
    public java.util.List<Job> getAllJobs() {
        return jobService.getAllJobs();
    }

    //Endpoint for deleting a job by id
    @GetMapping("/jobs/delete/{id}")
    public String deleteJobById(@PathVariable String id) {
        jobService.deleteJob(UUID.fromString(id));
        return "Job with id " + id + " deleted successfully";
    }

    @PostMapping("/jobs/create/bulk")
    public List<JobResponseDto> createJobsBulk(@RequestBody java.util.List<JobRequestDto> jobRequestDtos) {
        return jobService.createJobsBulk(jobRequestDtos);
    }
}

package com.akshadip.helios.repositories;

import com.akshadip.helios.enums.JobStatus;
import com.akshadip.helios.models.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Repository
public interface JobRepository extends JpaRepository<Job, UUID> {
    List<Job> findByNextFireAtBeforeAndStatus(LocalDateTime currentTime, JobStatus status);
}

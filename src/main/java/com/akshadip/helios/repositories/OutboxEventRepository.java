package com.akshadip.helios.repositories;

import com.akshadip.helios.enums.OutboxStatus;
import com.akshadip.helios.models.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {
    List<OutboxEvent> findByStatusAndRetryCountLessThan(OutboxStatus status, int maxRetryCount);
}

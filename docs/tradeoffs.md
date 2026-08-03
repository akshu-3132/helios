# Tradeoffs

This document explains the major architectural decisions made while designing Helios and the tradeoffs accepted as a result.

Helios was built to explore reliable distributed job scheduling rather than to provide a feature-complete workflow engine. Throughout the project, correctness and clear separation of responsibilities were generally prioritized over implementation simplicity.

---

## Distributed Coordination with etcd

### Why this approach?

In a distributed deployment, every application instance is capable of determining that a scheduled job is due. Without coordination, multiple instances could dispatch the same job simultaneously.

Helios uses **etcd leader election** to ensure that only one instance is responsible for scheduling jobs at any given time, while the remaining instances continue executing work.

### Benefits

* Prevents duplicate scheduling across multiple instances.
* Automatically elects a new scheduler if the current leader becomes unavailable.
* Separates scheduling responsibilities from execution.

### Tradeoffs

* Introduces another distributed system that must be deployed and monitored.
* Leader election adds operational complexity compared to a single-node scheduler.
* The scheduler becomes a logical bottleneck for dispatch decisions, although execution remains distributed.

---

## Apache Kafka for Job Distribution

### Why this approach?

Scheduling and execution are intentionally separated.

Once the leader determines that a job should run, it publishes an event to Kafka instead of executing the work directly. Worker instances independently consume these events and execute the corresponding jobs.

### Benefits

* Workers can scale horizontally without changing scheduling logic.
* Scheduling remains isolated from execution latency.
* Kafka provides durable message storage and consumer group coordination.

### Tradeoffs

* Execution becomes asynchronous rather than immediate.
* Operating Kafka increases infrastructure complexity.
* Message ordering and partitioning require additional design considerations.

---

## Transactional Outbox Pattern

### Why this approach?

Updating the database and publishing a Kafka message are two independent operations.

If the database update succeeds but Kafka publication fails, the scheduler and workers can observe inconsistent state.

Helios avoids this by first writing an outbox event as part of the same database transaction that updates the job state. A background publisher later delivers pending events to Kafka.

### Benefits

* Prevents database and Kafka from becoming inconsistent.
* Improves reliability during transient messaging failures.
* Makes failed publications retryable.

### Tradeoffs

* Adds an additional persistence layer.
* Introduces a small delay between scheduling and execution.
* Requires periodic polling of the outbox table.

---

## PostgreSQL as the System of Record

### Why this approach?

Helios stores job definitions, scheduling metadata, retry information, and outbox events in PostgreSQL.

Persisting scheduling state allows leadership to change without losing information about pending or incomplete work.

### Benefits

* Strong transactional guarantees.
* Reliable persistence of scheduling state.
* Supports structured relational data alongside flexible JSON payloads.

### Tradeoffs

* Database performance directly influences scheduling throughput.
* Horizontal write scaling requires additional database architecture.
* Database availability is critical for scheduler operation.

---

## Single Scheduler, Distributed Workers

### Why this approach?

Rather than allowing every application instance to compete for scheduled jobs, Helios elects a single scheduler while allowing all instances to participate in job execution.

This creates a clear separation of responsibilities:

* The leader decides **when** work should run.
* Workers decide **how** work is executed.

### Benefits

* Simpler scheduling logic.
* Eliminates duplicate dispatch decisions.
* Worker capacity can increase independently of scheduling.

### Tradeoffs

* Scheduling throughput depends on a single active leader.
* Leader failover briefly pauses scheduling while a new leader is elected.

---

## Reliability over Latency

Several design decisions intentionally prioritize reliability over minimum execution latency.

Examples include:

* Persisting scheduling state before dispatching work.
* Using the transactional outbox instead of publishing directly to Kafka.
* Executing jobs asynchronously through Kafka.

These choices introduce additional components and small processing delays but reduce the likelihood of inconsistent system state during failures.

---

## Current Limitations

Helios focuses on distributed scheduling concepts and does not yet implement every capability expected from a production scheduler.

Current limitations include:

* HTTP is the only supported job type.
* Retry policies are intentionally simple.
* Dead Letter Queue (DLQ) support has not yet been implemented.
* Observability is primarily application logging.
* The provided deployment uses single-node Kafka and etcd for local development rather than high availability.

These limitations were accepted to keep the project focused on the core scheduling architecture.

---

## Future Improvements

Potential areas for future development include:

* Dead Letter Queue support for permanently failed jobs.
* Configurable retry backoff strategies.
* Metrics and dashboards for scheduler and worker health.
* Distributed tracing across scheduling and execution.
* Multi-node Kafka and etcd deployments for higher availability.
* Support for additional job types beyond HTTP execution.

---

**Related docs:** [README](../README.md) · [Architecture](architecture.md)

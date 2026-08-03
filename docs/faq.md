## FAQ
## Why does `mvn test` fail?

Helios requires a unique `helios.instance.id` for leader election. When running tests, provide it as a JVM property:

```powershell
.\mvnw.cmd test "-Dhelios.instance.id=test-node"
```

## Why are there three Helios containers in compose?
They represent a multi-instance cluster where one node leads scheduling and others execute worker tasks.

## Does docker-compose include PostgreSQL?
No. The compose file includes Kafka, etcd, and three Helios app containers only. PostgreSQL is expected externally/host-side.

## Which node executes scheduled jobs?
The elected leader identifies due jobs and publishes them to Kafka. Worker instances consume these messages and execute the corresponding jobs.

## Where are Kafka topics configured?
Helios automatically creates the `jobs` topic during application startup.
## Are failed jobs retried?
Yes. Worker failures increment `jobs.retry_count`, and jobs are marked `FAILED` when retries reach `max_retries`.

## Is there dead-letter topic support?
No. Will be added in future versions. Currently, failed jobs are retried until `max_retries` is reached, after which they are marked as `FAILED`.

## What happens if the leader fails?

If the current leader becomes unavailable, etcd automatically elects a new leader. The newly elected instance resumes scheduling using the persisted state stored in PostgreSQL.

## Can Helios scale horizontally?

Yes. Additional worker instances can be added to increase job execution capacity. Leader election ensures that only one instance is responsible for scheduling jobs.

---
**Related docs:** [README](../README.md) · [Setup](setup.md)

**Doc owner:** `Akshadip`

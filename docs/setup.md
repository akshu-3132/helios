## Setup
This guide explains how to run Helios locally for development.
## Prerequisites
- java 25 (or compatible JDK)
- Maven (or use Maven Wrapper `mvnw` / `mvnw.cmd`)
- Docker & Docker Compose
- PostgreSQL running locally or accessible over the network

## Local installation
```powershell
cd d:\Projects\helios\helios
.\mvnw.cmd clean install
```

## Environment variables and properties
| Name | Purpose | Example | Required |
|---|---|---|---|
| `DB_PASS` | PostgreSQL password used by `spring.datasource.password` | `postgres` | Yes |
| `helios.instance.id` (JVM property) | Required instance identifier for etcd election candidate value | `helios-local-1` | Yes |
| `Profile to use during startup` | Profile selection (`docker` supported in repo) | `docker` | Optional |
| `spring.datasource.url` | JDBC URL | `jdbc:postgresql://localhost:5432/helios` | Yes |
| `spring.kafka.bootstrap-servers` | Kafka broker bootstrap | `localhost:9092` | Yes |
| `helios.etcd.endpoint` | etcd endpoint | `http://localhost:2379` | Yes |

> **Note:** Each Helios instance requires a unique `helios.instance.id` for leader election.## Database and migrations (Flyway)
Database migrations are managed automatically with Flyway.

Run migrations implicitly by starting the app, or explicitly via:
```powershell
cd d:\Projects\helios\helios
.\mvnw.cmd flyway:migrate
```

## etcd setup
Single-node etcd from compose:
```powershell
cd d:\Projects\helios\helios
docker compose up -d etcd
```

Defaults in compose:
- Client port: `2379`
- Endpoint used by app: `http://etcd:2379` (docker profile), `http://localhost:2379` (default profile)

## Kafka setup
Start Kafka from compose:
```powershell
cd d:\Projects\helios\helios
docker compose up -d kafka
```

Helios automatically creates the jobs topic during startup with :
- Partitions: 3
- Replicas: 1

Consumer group is configured as `worker-group`.

## PostgreSQL setup
PostgreSQL must be installed separately or provided by an external instance.

Example manual setup:
```sql
CREATE DATABASE helios;
```

## Running the project
### Local profile
```powershell
cd d:\Projects\helios\helios
$env:DB_PASS="your_password"
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.jvmArguments=-Dhelios.instance.id=helios-local-1"
```

### Docker profile (containerized app instances)
```powershell
cd d:\Projects\helios\helios
docker build -t helios .
$env:DB_PASS="your_password"
docker compose up -d
```

## Running tests
```powershell
cd d:\Projects\helios\helios
.\mvnw.cmd test "-Dhelios.instance.id=test-node"
```

When running tests, provide a helios.instance.id JVM property.

## Common setup errors and fixes
| Symptom | Likely cause | Fix |
|---|---|---|
| `Could not resolve placeholder 'helios.instance.id'` | Missing required property | Pass `-Dhelios.instance.id=...` |
| Kafka connection refused | Kafka not running on configured bootstrap server | Start Kafka or update `spring.kafka.bootstrap-servers` |
| etcd connection refused | etcd unavailable at configured endpoint | Start etcd or update `helios.etcd.endpoint` |
| Flyway migration failure | DB not reachable / wrong credentials / missing DB | Verify `spring.datasource.*` and create DB |
| Port 8081/8082/8083 conflicts | Existing services bound to ports | Adjust compose port mappings |


## Docker Compose

The Docker Compose configuration starts Kafka, etcd, and three Helios instances. PostgreSQL is expected to be provided separately.
PostgreSQL is expected to be available separately from the Docker Compose stack..

---
**Related docs:** [README](../README.md) · [Architecture](architecture.md) · [Deployment](deployment.md)

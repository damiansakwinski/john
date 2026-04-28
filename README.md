# John

A work in progress event-driven microservice that processes outbox events from animalhotels. Currently handles photo classification, detecting whether an offer's main photo contains a human face using AWS Bedrock (Claude).

The goal of this project is to learn enterprise application patterns like Change Data Capture (CDC), work hands on with Debezium and Kafka, and build a service designed to scale from the start.

## Architecture

```
┌──────────────┐       ┌──────────────┐       ┌──────────────┐       ┌──────────────┐
│ Animalhotels │       │   Debezium   │       │     John     │       │   Amazon     │
│   (PHP API)  │       │  (CDC via    │       │  (Spring     │       │   Bedrock    │
│              │       │  Kafka       │       │   Boot)      │       │  (Claude)    │
│  Main photo  │       │  Connect)    │       │              │       │              │
│  changed     │       │              │       │              │       │              │
│      │       │       │              │       │              │       │              │
│      ▼       │       │              │       │              │       │              │
│  Insert      │       │  Detect      │       │  Consume     │       │  Analyze     │
│  outbox  ────┼──────►│  outbox  ────┼──────►│  Kafka   ────┼──────►│  photo for   │
│  record      │       │  record      │       │  message     │       │  human face  │
│              │       │              │       │      │       │       │              │
└──────────────┘       └──────────────┘       │      ▼       │       └──────────────┘
                                              │  Store       │
                                              │  result in   │
                                              │  database    │
                                              └──────────────┘
```

- **Kafka consumer** - listens to Debezium CDC events from the `outbox` table via the EventRouter SMT
- **Idempotent processing** - event watermark table prevents out-of-order and duplicate processing
- **Dead letter queue** - failed messages are retried with exponential backoff (60s, 120s, 240s) then sent to a DLT topic
- **Database** - owns tables prefixed with `john_` in the shared AnimalHotels PostgreSQL database

## Running

### With Docker Compose (from the project root)

```bash
docker compose up john kafka kafka-connect database
```

### Rebuild after code changes

```bash
docker compose up john --build
```

### Environment variables

| Variable | Default        | Description |
|---|----------------|---|
| `POSTGRES_HOST` | `database`     | PostgreSQL host |
| `POSTGRES_POOL_1_PORT` | `5432`         | PostgreSQL port |
| `POSTGRES_POOL_1_DB` | `api`          | Database name |
| `POSTGRES_USER` | `john`         | Database user |
| `POSTGRES_PASSWORD` | `!ChangeMe!`   | Database password |
| `POSTGRES_SSLMODE` | `disable`      | SSL mode |
| `KAFKA_HOST` | `kafka`        | Kafka broker host |
| `KAFKA_PORT` | `9092`         | Kafka broker port |
| `AWS_ACCESS_KEY_ID` | -              | AWS credentials for Bedrock |
| `AWS_SECRET_ACCESS_KEY` | -              | AWS credentials for Bedrock |
| `AWS_REGION` | `eu-central-1` | AWS region |
| `KAFKA_LISTENER_PHOTO_CLASSIFIER_CONCURRENCY` | `3` | Number of concurrent consumer threads |

## Testing

```bash
./gradlew test
```

Requires a running PostgreSQL instance on `localhost:5432`.

## Kafka topics

| Topic | Description |
|---|---|
| `outbox.OfferPhoto` | Debezium CDC events for offer photo changes |
| `outbox.OfferPhoto-retry-*` | Retry topics (managed by Spring Kafka) |
| `outbox.OfferPhoto-dlt` | Dead letter topic for failed messages |

## Scaling

To increase throughput, scale the number of Kafka partitions and consumer threads.

### Increase partitions

```bash
docker exec kafka-kafka-1 /opt/kafka/bin/kafka-topics.sh --bootstrap-server localhost:9092 --alter --topic outbox.OfferPhoto --partitions 12
```

Partitions can only be increased, never decreased. All events for the same offer are keyed by offer ID, so ordering per offer is preserved regardless of partition count.

### Increase consumer concurrency

Set `KAFKA_LISTENER_PHOTO_CLASSIFIER_CONCURRENCY` to the desired number of threads and restart John. Maximum useful concurrency equals the number of partitions.

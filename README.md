# John

A work in progress event-driven microservice that processes outbox events from animalhotels. Currently handles photo classification, detecting whether an offer's main photo contains a human face using AWS Bedrock (Claude).

## Architecture

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

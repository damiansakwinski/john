#!/bin/bash

echo "Waiting for Kafka at ${KAFKA_HOST}:${KAFKA_PORT}..."
until bash -c "echo >/dev/tcp/${KAFKA_HOST}/${KAFKA_PORT}" 2>/dev/null; do
  sleep 2
done
echo "Kafka is ready."

exec java -javaagent:/usr/local/newrelic/newrelic.jar -Dnewrelic.environment=${APP_ENV:-production} -jar app.jar

#!/bin/sh

echo "Waiting for Kafka Connect to be ready..."
while [ "$(curl -s -o /dev/null -w '%{http_code}' http://localhost:8083/connectors)" != "200" ]; do
  sleep 2
done

echo "Substituting env vars in connector config..."
envsubst '$POSTGRES_HOST $POSTGRES_PORT $POSTGRES_USER $POSTGRES_PASSWORD $POSTGRES_DB' < /app/config/outbox-connector.json > /tmp/connector.json

echo "Registering connector..."
curl -X PUT http://localhost:8083/connectors/animalhotels-outbox-connector/config \
  -H "Content-Type: application/json" \
  -d @/tmp/connector.json
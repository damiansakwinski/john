FROM debezium/connect:2.5

USER root
RUN microdnf install -y gettext && microdnf clean all
COPY config/ /app/config/
COPY register-connector.sh /app/register-connector.sh
COPY docker-entrypoint.sh /app/docker-entrypoint.sh
RUN chmod +x /app/docker-entrypoint.sh /app/register-connector.sh

USER 1001

ENTRYPOINT ["/app/docker-entrypoint.sh"]
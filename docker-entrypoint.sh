#!/bin/sh

/app/register-connector.sh &
exec /docker-entrypoint.sh start

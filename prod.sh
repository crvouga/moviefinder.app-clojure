#!/bin/bash

DBMATE_VERSION="1.14.0"
DBMATE_URL="https://github.com/amacneil/dbmate/releases/download/v${DBMATE_VERSION}/dbmate-linux-amd64"

echo "Downloading dbmate version ${DBMATE_VERSION}..."
curl -L -o dbmate ${DBMATE_URL}

chmod +x dbmate

./dbmate up

rm dbmate

echo "dbmate up executed successfully."

echo "Starting the application..."

lein run

echo "Application started successfully."

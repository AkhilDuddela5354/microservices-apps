#!/bin/bash
# Build the Docker image for the asset service
echo "Building Docker image for telemetry service..."
docker buildx build -t ram1uj/telemetry-service:latest --platform linux/amd64,linux/arm64 --push .
echo "Docker image for telemetry service built and pushed successfully!"
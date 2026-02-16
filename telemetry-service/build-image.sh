#!/bin/bash
# Build the Docker image for the asset service
echo "Building Docker image for telemetry service..."
docker buildx build -t telemetry-service:latest .
echo "Docker image for telemetry service built successfully!"
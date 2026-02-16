#!/bin/bash
# Build the Docker image for the asset service
echo "Building Docker image for config-server service..."
docker buildx build -t config-server:latest .
echo "Docker image for config-server service built successfully!"
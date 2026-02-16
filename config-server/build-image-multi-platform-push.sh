#!/bin/bash
# Build the Docker image for the asset service
echo "Building Docker image for config-server service..."
docker buildx build -t ram1uj/config-server:latest --platform linux/amd64,linux/arm64 --push .
echo "Docker image for config-server service built and pushed successfully!"
#!/bin/bash
# Build the Docker image for the gateway service
echo "Building Docker image for gateway service..."
docker buildx build -t ram1uj/gateway-service:latest --platform linux/amd64,linux/arm64 --push .
echo "Docker image for gateway service built and pushed successfully!"
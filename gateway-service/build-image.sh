#!/bin/bash
# Build the Docker image for the asset service
echo "Building Docker image for gateway service..."
docker buildx build -t gateway-service:latest .
echo "Docker image for gateway service built successfully!"
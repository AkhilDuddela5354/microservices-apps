#!/bin/bash
# Build the Docker image for the asset service
echo "Building Docker image for asset service..."
docker buildx build -t asset-service:latest .
echo "Docker image for asset service built successfully!"
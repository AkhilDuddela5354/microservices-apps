#!/bin/bash
# Build the Docker image for the asset service
echo "Building Docker image for asset service..."
docker buildx build -t ram1uj/asset-service:latest --platform linux/amd64,linux/arm64 --push .
echo "Docker image for asset service built and pushed successfully!"
# Docker Container Setup Guide

## 🐳 Running Microservices with Docker Compose

This guide shows you how to run all microservices as Docker containers with the Config Server.

---

## 📋 Prerequisites

- Docker installed and running
- Docker Compose installed
- All microservices updated to use Config Server
- Config-server repository pushed to GitHub

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                     Docker Network (greenko-network)          │
│                                                               │
│  ┌──────────────────┐         ┌─────────────────────────┐  │
│  │  Config Server   │◄────────┤  All Microservices      │  │
│  │  Port: 8888      │         │  fetch configs from     │  │
│  │                  │         │  Config Server          │  │
│  └──────────────────┘         └─────────────────────────┘  │
│           │                                                   │
│           │ Fetches configs from                             │
│           ▼                                                   │
│  ┌──────────────────────────────────────┐                   │
│  │  GitHub Repository                    │                   │
│  │  test-config-server                   │                   │
│  └──────────────────────────────────────┘                   │
│                                                               │
│  ┌──────────────┐  ┌─────────────────┐  ┌───────────────┐ │
│  │ Asset        │  │ Telemetry       │  │ Alert         │ │
│  │ Service      │  │ Service         │  │ Service       │ │
│  │ Port: 8081   │  │ Port: 8082      │  │ Port: 8083    │ │
│  └──────────────┘  └─────────────────┘  └───────────────┘ │
│           │                 │                   │            │
│           └─────────────────┴───────────────────┘            │
│                             │                                 │
│                   ┌─────────▼──────────┐                     │
│                   │  Gateway Service   │                     │
│                   │  Port: 8080        │                     │
│                   └────────────────────┘                     │
│                                                               │
└─────────────────────────────────────────────────────────────┘
                            ▲
                            │
                    External Access
                    localhost:8080
```

---

## 🚀 Quick Start Commands

### 1. Build and Start All Services
```bash
cd /home/akhilduddela/test/greenko-level-2/microservices-apps

# Build and start all services
docker-compose up --build -d
```

### 2. View Logs
```bash
# View all logs
docker-compose logs -f

# View specific service logs
docker-compose logs -f config-server
docker-compose logs -f asset-service
docker-compose logs -f telemetry-service
docker-compose logs -f alert-service
docker-compose logs -f gateway-service
```

### 3. Check Service Status
```bash
docker-compose ps
```

### 4. Stop All Services
```bash
docker-compose down
```

### 5. Stop and Remove Everything (including images)
```bash
docker-compose down --rmi all --volumes
```

---

## 📝 Startup Order

The services start in this order (managed by `depends_on`):

1. **Config Server** (8888)
   - Starts first
   - Healthcheck ensures it's ready
   - Clones configuration from GitHub

2. **Asset Service** (8081)
   - Waits for Config Server health check
   - Fetches configuration from Config Server

3. **Telemetry Service** (8082)
   - Waits for Config Server health check
   - Waits for Asset Service to start
   - Fetches configuration from Config Server

4. **Alert Service** (8083)
   - Waits for Config Server health check
   - Waits for Telemetry Service to start
   - Fetches configuration from Config Server

5. **Gateway Service** (8080)
   - Waits for all other services
   - Routes requests to all backend services

---

## 🔍 Service Endpoints

### Config Server
- **Health**: http://localhost:8888/actuator/health
- **Config for asset-service**: http://localhost:8888/asset-service/dev
- **Config for telemetry-service**: http://localhost:8888/telemetry-service/dev
- **Config for alert-service**: http://localhost:8888/alert-service/dev
- **Config for gateway-service**: http://localhost:8888/gateway-service/default

### Gateway (API Gateway)
- **Base URL**: http://localhost:8080
- **Asset API**: http://localhost:8080/api/assets
- **Telemetry API**: http://localhost:8080/api/telemetry
- **Alert API**: http://localhost:8080/api/alerts
- **Swagger UI**: http://localhost:8080/swagger-ui.html

### Direct Service Access (if needed)
- **Asset Service**: http://localhost:8081
- **Telemetry Service**: http://localhost:8082
- **Alert Service**: http://localhost:8083

---

## 🐳 Docker Commands Cheat Sheet

### Building Services
```bash
# Build all services
docker-compose build

# Build specific service
docker-compose build config-server
docker-compose build asset-service

# Build with no cache (force rebuild)
docker-compose build --no-cache
```

### Starting Services
```bash
# Start all services in background
docker-compose up -d

# Start all services with logs visible
docker-compose up

# Start specific service
docker-compose up -d asset-service

# Scale a service (if needed)
docker-compose up -d --scale asset-service=3
```

### Stopping Services
```bash
# Stop all services
docker-compose stop

# Stop specific service
docker-compose stop asset-service

# Stop and remove containers
docker-compose down

# Stop, remove containers, networks, volumes, and images
docker-compose down --rmi all --volumes
```

### Viewing Logs
```bash
# Follow all logs
docker-compose logs -f

# Follow specific service logs
docker-compose logs -f config-server

# View last 100 lines
docker-compose logs --tail=100

# View logs with timestamps
docker-compose logs -f --timestamps
```

### Service Management
```bash
# Restart all services
docker-compose restart

# Restart specific service
docker-compose restart asset-service

# Check service status
docker-compose ps

# Execute command in running container
docker-compose exec asset-service sh

# View resource usage
docker stats
```

### Troubleshooting
```bash
# Check container details
docker-compose ps -a

# Inspect specific container
docker inspect <container_id>

# View container logs
docker logs <container_id>

# Access container shell
docker exec -it <container_name> sh

# Check network connectivity
docker network ls
docker network inspect greenko-microservices_greenko-network
```

---

## 🔧 Configuration

### Environment Variables

Each service can be configured via environment variables in `docker-compose.yml`:

```yaml
environment:
  SPRING_PROFILES_ACTIVE: dev          # Profile to use (dev/prod)
  SPRING_CONFIG_IMPORT: configserver:http://config-server:8888  # Config Server URL
```

### Network Configuration

All services are on the same Docker network (`greenko-network`), allowing them to communicate using service names as hostnames.

---

## 🔄 Updating Configurations

### Method 1: Update GitHub Repository
```bash
# 1. Update configs in test-config-server
cd test-config-server
vim services/asset-service/asset-service-dev.yaml

# 2. Commit and push
git add .
git commit -m "Update asset-service config"
git push origin main

# 3. Restart config-server to fetch latest
docker-compose restart config-server

# 4. Refresh microservices
curl -X POST http://localhost:8081/actuator/refresh
curl -X POST http://localhost:8082/actuator/refresh
curl -X POST http://localhost:8083/actuator/refresh
```

### Method 2: Rebuild Specific Service
```bash
# If you updated application code
docker-compose build asset-service
docker-compose up -d asset-service
```

---

## 🧪 Testing the Setup

### 1. Health Checks
```bash
# Config Server
curl http://localhost:8888/actuator/health

# Asset Service
curl http://localhost:8081/actuator/health

# Telemetry Service
curl http://localhost:8082/actuator/health

# Alert Service
curl http://localhost:8083/actuator/health

# Gateway Service
curl http://localhost:8080/actuator/health
```

### 2. Test Configuration Fetching
```bash
# Verify configs are being fetched from GitHub
curl http://localhost:8888/asset-service/dev | jq
```

### 3. Test API Endpoints via Gateway
```bash
# Asset API via Gateway
curl http://localhost:8080/api/assets

# Telemetry API via Gateway
curl http://localhost:8080/api/telemetry

# Alert API via Gateway
curl http://localhost:8080/api/alerts
```

---

## 📊 Monitoring

### View Real-time Logs
```bash
# All services
docker-compose logs -f

# Specific pattern
docker-compose logs -f | grep ERROR
```

### Resource Usage
```bash
# CPU and Memory usage
docker stats

# Specific container
docker stats config-server
```

### Service Health
```bash
# Check all container statuses
docker-compose ps

# Detailed health status
docker inspect --format='{{.State.Health.Status}}' config-server
```

---

## 🐛 Troubleshooting Guide

### Config Server Not Starting
```bash
# Check logs
docker-compose logs config-server

# Common issues:
# - GitHub authentication failed: Check repository URL
# - Port 8888 already in use: Stop other services on that port
# - Network issues: Check internet connectivity
```

### Service Can't Connect to Config Server
```bash
# Check if config-server is healthy
docker-compose ps config-server

# Check network connectivity
docker-compose exec asset-service ping config-server

# Verify environment variable
docker-compose exec asset-service env | grep SPRING_CONFIG_IMPORT
```

### Service Crashes on Startup
```bash
# View crash logs
docker-compose logs asset-service

# Common issues:
# - Config not found: Check service name matches config files
# - Config Server not ready: Wait for health check to pass
# - Port conflicts: Check if ports are already in use
```

### Slow Startup
```bash
# Config Server needs time to clone repository
# Wait for health check to pass (~30-40 seconds)
docker-compose logs -f config-server | grep "Started"
```

---

## 🔐 Production Considerations

### 1. Environment-Specific Configs
```bash
# For production, use prod profile
docker-compose up -d -e SPRING_PROFILES_ACTIVE=prod
```

### 2. Secrets Management
- Don't commit sensitive data to Git
- Use Docker secrets or environment variables
- Consider using Spring Cloud Vault

### 3. Database
Uncomment MySQL service in docker-compose.yml for production:
```bash
# Uncomment mysql service section in docker-compose.yml
docker-compose up -d mysql
```

### 4. Scaling
```bash
# Scale services horizontally
docker-compose up -d --scale asset-service=3 --scale telemetry-service=2
```

---

## 📚 Additional Docker Compose Files

### Development (docker-compose.override.yml)
Create this file for development-specific overrides:
```yaml
services:
  config-server:
    environment:
      SPRING_PROFILES_ACTIVE: native
      SPRING_CLOUD_CONFIG_SERVER_NATIVE_SEARCH_LOCATIONS: file:/config
    volumes:
      - ../test-config-server:/config
```

### Production (docker-compose.prod.yml)
```bash
# Use production config
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d
```

---

## ✅ Success Checklist

- [ ] Config Server starts successfully and is healthy
- [ ] Config Server can fetch configurations from GitHub
- [ ] Asset Service starts and connects to Config Server
- [ ] Telemetry Service starts and connects to Config Server
- [ ] Alert Service starts and connects to Config Server
- [ ] Gateway Service starts and routes correctly
- [ ] All health endpoints return UP status
- [ ] API endpoints accessible via Gateway (port 8080)
- [ ] Swagger UI accessible at http://localhost:8080/swagger-ui.html

---

## 🎯 Summary

**To start everything:**
```bash
cd /home/akhilduddela/test/greenko-level-2/microservices-apps
docker-compose up --build -d
docker-compose logs -f
```

**To stop everything:**
```bash
docker-compose down
```

**Access your services:**
- Gateway API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- Config Server: http://localhost:8888/actuator/health

🎉 **Your microservices are now running in Docker containers with centralized configuration!**

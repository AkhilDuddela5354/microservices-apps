# API Gateway Service - Testing and Usage Guide

## ✅ Gateway Service Status: **RUNNING**

The API Gateway is successfully running and routing requests to all microservices.

## Service Architecture

```
                    ┌─────────────────────┐
                    │   API Gateway       │
                    │   Port: 8080        │
                    └──────────┬──────────┘
                               │
              ┌────────────────┼────────────────┐
              │                │                │
    ┌─────────▼─────────┐ ┌───▼────────┐ ┌────▼──────────┐
    │  Asset Service    │ │ Telemetry  │ │ Alert Service │
    │  Port: 8081       │ │ Port: 8082 │ │ Port: 8083    │
    └───────────────────┘ └────────────┘ └───────────────┘
```

## Running Services

| Service | Status | Direct Port | Gateway Path |
|---------|--------|-------------|--------------|
| **Gateway** | ✅ Running | 8080 | - |
| **Alert Service** | ✅ Running | 8083 | `/api/alerts/**` |
| **Asset Service** | ✅ Running | 8081 | `/api/assets/**` |
| **Telemetry Service** | ✅ Running | 8082 | `/api/telemetry/**` |
| **MySQL** | ✅ Running | 3306 | - |

## Gateway Routes Configuration

### 1. Alert Service Route
- **Path Pattern**: `/api/alerts/**`
- **Target**: `http://alert-service:8080`
- **Examples**:
  ```bash
  # Get all alerts via gateway
  curl http://localhost:8080/api/alerts
  
  # Get critical alerts
  curl http://localhost:8080/api/alerts/severity/CRITICAL
  
  # Create new alert
  curl -X POST http://localhost:8080/api/alerts \
    -H "Content-Type: application/json" \
    -d '{
      "title": "Test Alert",
      "message": "Testing via gateway",
      "severity": "INFO",
      "targetService": "asset-service"
    }'
  ```

### 2. Asset Service Route
- **Path Pattern**: `/api/assets/**`
- **Target**: `http://asset-service:8080`
- **Examples**:
  ```bash
  # Get all assets via gateway
  curl http://localhost:8080/api/assets
  
  # Get specific asset
  curl http://localhost:8080/api/assets/1
  ```

### 3. Telemetry Service Route
- **Path Pattern**: `/api/telemetry/**`
- **Target**: `http://telemetry-service:8080`
- **Examples**:
  ```bash
  # Get all telemetry data via gateway
  curl http://localhost:8080/api/telemetry
  
  # Create telemetry data
  curl -X POST http://localhost:8080/api/telemetry \
    -H "Content-Type: application/json" \
    -d '{
      "assetId": 1,
      "temperature": 75.5,
      "humidity": 60.0
    }'
  ```

## Testing Results

### ✅ Alert Service Through Gateway
```bash
curl http://localhost:8080/api/alerts | jq 'length'
# Returns: 8 alerts
```

### ✅ Asset Service Through Gateway
```bash
curl http://localhost:8080/api/assets | jq 'length'
# Returns: 12 assets
```

### ✅ Telemetry Service Through Gateway
```bash
curl http://localhost:8080/api/telemetry | jq 'length'
# Returns: 0 (no telemetry data yet)
```

## Benefits of Using the Gateway

1. **Single Entry Point**: All services accessible through one port (8080)
2. **Service Discovery**: Gateway handles routing to internal services
3. **Load Balancing**: Can distribute requests across multiple instances
4. **Security**: Centralized authentication and authorization point
5. **Monitoring**: Single point to monitor all API traffic
6. **API Versioning**: Easy to manage API versions

## Direct Access vs Gateway Access

### Direct Access (Still Available)
- Alert Service: `http://localhost:8083/api/alerts`
- Asset Service: `http://localhost:8081/api/assets`
- Telemetry Service: `http://localhost:8082/api/telemetry`

### Gateway Access (Recommended)
- Alert Service: `http://localhost:8080/api/alerts`
- Asset Service: `http://localhost:8080/api/assets`
- Telemetry Service: `http://localhost:8080/api/telemetry`

## Gateway Configuration Files

### Application Configuration
Location: `gateway-service/src/main/resources/application.yaml`

```yaml
spring:
  application:
    name: gateway-service
  cloud:
    gateway:
      server:
        webflux:
          routes:
            - id: asset-service
              uri: ${ASSET_SERVICE_URL:http://localhost:8081}
              predicates:
                - Path=/api/assets/**
            - id: telemetry-service
              uri: ${TELEMETRY_SERVICE_URL:http://localhost:8082}
              predicates:
                - Path=/api/telemetry/**
            - id: alert-service
              uri: ${ALERT_SERVICE_URL:http://localhost:8083}
              predicates:
                - Path=/api/alerts/**
```

### Docker Configuration
Location: `microservices-apps/docker-compose.yml`

The gateway service is configured with environment variables pointing to internal services.

## Starting the Gateway

### Method 1: Using Docker Compose (Recommended)
```bash
cd microservices-apps
docker compose up -d gateway-service
```

### Method 2: Using Docker Run
```bash
docker run -d --name gateway-service \
  --network asset-telemetry-services_asset-telemetry-network \
  -p 8080:8080 \
  -e ASSET_SERVICE_URL=http://asset-service:8080 \
  -e TELEMETRY_SERVICE_URL=http://telemetry-service:8080 \
  -e ALERT_SERVICE_URL=http://alert-service:8080 \
  gateway-service
```

## Gateway Logs

View gateway logs:
```bash
docker logs gateway-service

# Follow logs in real-time
docker logs -f gateway-service

# Last 50 lines
docker logs gateway-service --tail 50
```

## Troubleshooting

### Gateway Not Responding
1. Check if container is running:
   ```bash
   docker ps | grep gateway-service
   ```

2. Check gateway logs:
   ```bash
   docker logs gateway-service
   ```

3. Verify network connectivity:
   ```bash
   docker exec gateway-service ping asset-service
   ```

### Route Not Working
1. Verify path pattern matches your request
2. Check if target service is running
3. Review gateway logs for routing errors

## Health Check

Check if gateway is responding:
```bash
# Should return connection established
curl -v http://localhost:8080
```

## Performance Testing

Test gateway performance with multiple requests:
```bash
# Get alerts 10 times through gateway
for i in {1..10}; do
  echo "Request $i:"
  time curl -s http://localhost:8080/api/alerts | jq 'length'
done
```

## Summary

✅ **Gateway Service**: Fully operational on port 8080
✅ **All Routes**: Successfully routing to backend services
✅ **Alert Service**: 8 alerts accessible through gateway
✅ **Asset Service**: 12 assets accessible through gateway
✅ **Telemetry Service**: Ready to receive data through gateway

The API Gateway is your single entry point for all microservices! 🎉

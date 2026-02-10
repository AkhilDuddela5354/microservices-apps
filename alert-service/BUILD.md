# Alert Service - Build and Deployment Instructions

## Quick Start

### 1. Build the Docker Image
```bash
cd /home/akhilduddela/test/greenko-level-2/microservices-apps/alert-service
docker buildx build -t alert-service .
```

### 2. Run All Services with Docker Compose
```bash
cd /home/akhilduddela/test/greenko-level-2/microservices-apps
docker-compose up -d
```

### 3. Run Only Alert Service
```bash
docker-compose up alert-service
```

## Service Details

- **Service Name**: alert-service
- **Port**: 8083 (mapped to internal 8080)
- **Network**: asset-telemetry-network
- **Database**: H2 in-memory (for development)

## Testing the Service

### Create an Alert
```bash
curl -X POST http://localhost:8083/api/alerts \
  -H "Content-Type: application/json" \
  -d '{
    "title": "System Alert",
    "message": "This is a test alert",
    "severity": "INFO",
    "targetService": "asset-service"
  }'
```

### Get All Alerts
```bash
curl http://localhost:8083/api/alerts
```

### Get Alerts by Status
```bash
curl http://localhost:8083/api/alerts/status/PENDING
curl http://localhost:8083/api/alerts/status/SENT
```

### Get Alerts by Service
```bash
curl http://localhost:8083/api/alerts/service/asset-service
curl http://localhost:8083/api/alerts/service/telemetry-service
```

### Get Alerts by Severity
```bash
curl http://localhost:8083/api/alerts/severity/CRITICAL
curl http://localhost:8083/api/alerts/severity/WARNING
```

## Access Points

- **API**: http://localhost:8083/api/alerts
- **Swagger UI**: http://localhost:8083/swagger-ui.html
- **H2 Console**: http://localhost:8083/h2-console

## Integration Example

Other services can send alerts using:

```bash
# From within the Docker network
curl -X POST http://alert-service:8080/api/alerts \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Asset Threshold Alert",
    "message": "Asset temperature exceeded 80°C",
    "severity": "WARNING",
    "targetService": "asset-service"
  }'
```

## Severity Levels

- **INFO**: Informational messages
- **WARNING**: Warning conditions
- **ERROR**: Error conditions
- **CRITICAL**: Critical conditions requiring immediate attention

## Alert Status

- **PENDING**: Alert created but not yet sent
- **SENT**: Alert successfully sent to target service
- **FAILED**: Alert sending failed (check errorMessage field)

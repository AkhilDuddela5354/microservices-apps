# Alert Service

A microservice for sending alerts and notifications to other services in the Greenko ecosystem.

## Overview

The Alert Service is responsible for:
- Creating and managing alerts
- Tracking alert delivery status
- Supporting multiple severity levels (INFO, WARNING, ERROR, CRITICAL)
- Sending notifications to target services

## Features

- **Alert Management**: Create, view, and track alerts
- **Severity Levels**: Support for INFO, WARNING, ERROR, and CRITICAL alerts
- **Target Services**: Send alerts to specific services
- **Status Tracking**: Track alert status (PENDING, SENT, FAILED)
- **H2 Database**: In-memory database for development
- **REST API**: Full REST API with Swagger documentation

## Technology Stack

- Spring Boot 4.0.2
- Spring Data JPA
- Spring Cloud OpenFeign
- H2 Database
- Lombok
- Java 21

## API Endpoints

### Create Alert
```
POST /api/alerts
Content-Type: application/json

{
  "title": "Asset Threshold Exceeded",
  "message": "Asset temperature exceeded normal threshold",
  "severity": "WARNING",
  "targetService": "asset-service"
}
```

### Get All Alerts
```
GET /api/alerts
```

### Get Alerts by Status
```
GET /api/alerts/status/{status}
```
Example: `/api/alerts/status/PENDING`

### Get Alerts by Service
```
GET /api/alerts/service/{service}`
```
Example: `/api/alerts/service/asset-service`

### Get Alerts by Severity
```
GET /api/alerts/severity/{severity}
```
Example: `/api/alerts/severity/CRITICAL`

## Building and Running

### Build with Docker
```bash
cd microservices-apps/alert-service
docker buildx build -t alert-service .
```

### Run with Docker Compose
```bash
cd microservices-apps
docker-compose up alert-service
```

The service will be available at `http://localhost:8083`

### API Documentation
Access Swagger UI at: `http://localhost:8083/swagger-ui.html`

### H2 Console
Access H2 Console at: `http://localhost:8083/h2-console`
- JDBC URL: `jdbc:h2:mem:alertdb`
- Username: `sa`
- Password: (leave empty)

## Configuration

### Environment Variables
- `SPRING_PROFILES_ACTIVE`: Active profile (default: dev)

### Profiles
- **dev**: Development profile with H2 database and debug logging

## Integration with Other Services

The Alert Service can be called by other services to send notifications:

```java
// Example from Asset Service
@Autowired
private RestTemplate restTemplate;

public void sendAlert(String title, String message, String severity) {
    AlertRequest request = new AlertRequest(title, message, severity, "asset-service");
    restTemplate.postForEntity(
        "http://alert-service:8080/api/alerts",
        request,
        AlertResponse.class
    );
}
```

## Database Schema

### alerts table
- `id`: Primary key
- `title`: Alert title
- `message`: Alert message (up to 1000 characters)
- `severity`: Alert severity (INFO, WARNING, ERROR, CRITICAL)
- `target_service`: Target service to receive the alert
- `status`: Alert status (PENDING, SENT, FAILED)
- `created_at`: Alert creation timestamp
- `sent_at`: Alert sent timestamp
- `error_message`: Error message if sending failed

## Future Enhancements

- Implement actual service-to-service communication using Feign clients
- Add webhook support for external notifications
- Implement email/SMS notifications
- Add alert templates
- Implement retry mechanism for failed alerts
- Add alert scheduling capabilities

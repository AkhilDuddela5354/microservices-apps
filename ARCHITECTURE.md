# Greenko Microservices - Complete Architecture Documentation

## 📐 System Architecture Overview

```
                                    ┌─────────────────┐
                                    │   GitHub Repo   │
                                    │ (test-config-   │
                                    │    server)      │
                                    └────────┬────────┘
                                             │ Git Clone
                                             ▼
┌──────────────────────────────────────────────────────────────────────────┐
│                          Docker Network: greenko-network                  │
│                                                                           │
│  ┌─────────────────┐         ┌──────────────────────────────────┐       │
│  │  Config Server  │◄────────│  External Git Configuration      │       │
│  │   Port: 8888    │         │  - Global configs                │       │
│  └────────┬────────┘         │  - Service-specific configs      │       │
│           │                  │  - Environment configs (dev/prod)│       │
│           │ Fetch Config     └──────────────────────────────────┘       │
│           ▼                                                              │
│  ┌─────────────────────────────────────────────────────────────┐        │
│  │              Gateway Service (Port 8080)                     │        │
│  │  - Routes all external requests                             │        │
│  │  - StripPrefix filter for service paths                     │        │
│  │  - Swagger UI aggregation                                   │        │
│  └──┬──────────────┬──────────────┬────────────────────────────┘        │
│     │              │              │                                      │
│     ▼              ▼              ▼                                      │
│  ┌────────┐   ┌──────────┐   ┌────────┐                               │
│  │ Asset  │   │Telemetry │   │ Alert  │                               │
│  │Service │───▶│ Service  │───▶│Service │                               │
│  │8081    │   │  8082    │   │  8083  │                               │
│  └───┬────┘   └─────┬────┘   └────┬───┘                               │
│      │              │              │                                    │
│  ┌───▼──────────────▼──────────────▼───┐                              │
│  │         H2 In-Memory Databases       │                              │
│  │   (assets, telemetry, alerts)        │                              │
│  └──────────────────────────────────────┘                              │
└──────────────────────────────────────────────────────────────────────────┘
```

## 🔄 Complete Request Flow

### 1. Application Startup Sequence
```
1. Config Server starts → Clones Git repo → Serves configurations
2. Asset Service starts → Fetches config from Config Server → Initializes H2 DB
3. Telemetry Service starts → Fetches config → Connects to Asset Service
4. Alert Service starts → Fetches config → Connects to Telemetry Service
5. Gateway starts → Fetches routes config → Ready to serve requests
```

### 2. API Request Flow (Example: Get Assets)
```
Client Request
    ↓
http://localhost:8080/api/assets  (Gateway)
    ↓
Gateway applies Path=/api/assets/** predicate
    ↓
Routes to: http://asset-service:8080/api/assets
    ↓
Asset Service processes request
    ↓
Queries H2 database
    ↓
Returns JSON response
    ↓
Gateway forwards to client
```

### 3. Configuration Refresh Flow
```
Developer pushes config changes to Git
    ↓
Config Server pulls latest from Git (on demand or scheduled)
    ↓
POST http://localhost:8081/actuator/refresh
    ↓
Service reloads configuration without restart
```

## 🔗 Service Dependencies & Communication

### Dependency Chain
```
Gateway Service
    ├── depends_on: config-server (healthy)
    ├── Routes to: asset-service, telemetry-service, alert-service
    
Asset Service  
    └── depends_on: config-server (healthy)
    
Telemetry Service
    ├── depends_on: config-server (healthy)
    ├── depends_on: asset-service (started)
    └── calls: asset-service via RestTemplate
    
Alert Service
    ├── depends_on: config-server (healthy)  
    ├── depends_on: telemetry-service (started)
    └── calls: telemetry-service via RestTemplate
```

### Service URLs (Docker Network)
- Config Server: `http://config-server:8888`
- Asset Service: `http://asset-service:8080`
- Telemetry Service: `http://telemetry-service:8080`
- Alert Service: `http://alert-service:8080`
- Gateway: `http://gateway-service:8080`

## 📦 Maven Dependencies Explained

### Config Server Dependencies
```xml
spring-cloud-config-server       → Serves configurations from Git
spring-boot-starter-actuator     → Health checks & monitoring
```

### Microservices (Asset, Telemetry, Alert) Dependencies
```xml
spring-boot-starter-web          → REST API capabilities
spring-boot-starter-data-jpa     → Database persistence
spring-cloud-starter-config      → Config Server client
spring-boot-starter-actuator     → Health & metrics endpoints
springdoc-openapi-starter        → Swagger/OpenAPI documentation
h2                               → In-memory database
mysql-connector-j                → MySQL driver (optional)
```

### Gateway Dependencies
```xml
spring-cloud-starter-gateway-server-webflux  → Reactive API Gateway
spring-cloud-starter-config                  → Config Server client
spring-boot-starter-actuator                 → Health endpoints
springdoc-openapi-starter-webflux-ui         → Swagger UI aggregation
```

## 🐳 Docker Configuration Deep Dive

### Multi-Stage Dockerfile (All Services)
```dockerfile
# Stage 1: Build
FROM maven:3.9.11-eclipse-temurin-25 AS build
- Copies pom.xml and resolves dependencies (cached layer)
- Copies source code
- Builds JAR file with Maven

# Stage 2: Runtime  
FROM eclipse-temurin:25-jdk
- Uses smaller runtime image
- Copies only the JAR file
- Exposes service port
- Runs the application
```

### Docker Compose Features

**Networks:**
```yaml
greenko-network → All services communicate via this network
                → Services resolve each other by container name
```

**Health Checks:**
```yaml
config-server → curl -f http://localhost:8888/actuator/health
              → Must pass before other services start
```

**Environment Variables:**
```yaml
SPRING_PROFILES_ACTIVE    → Activates dev/prod profiles
SPRING_CONFIG_IMPORT      → Points to Config Server
SERVICE_URL variables     → Inter-service communication URLs
```

**Volume Mounting (Optional):**
```yaml
Currently: None (H2 in-memory)
MySQL option: mysql-data:/var/lib/mysql → Persistent storage
```

## 🗄️ Database Architecture

### Development (H2 In-Memory)
```
asset-service       → jdbc:h2:mem:assets
telemetry-service   → jdbc:h2:mem:telemetry  
alert-service       → jdbc:h2:mem:alerts

⚠️ Data lost on container restart
✅ Fast, no setup required
```

### Production (MySQL - Optional)
```
Uncomment mysql service in docker-compose.yml
All services connect to: greenko-mysql:3306
Uses persistent volume: mysql-data
```

## 🔧 Configuration Management

### Configuration Hierarchy
```
1. Global (application.yml)
   └── Applied to ALL services
   
2. Service-specific (asset-service.yaml)  
   └── Applied to specific service (any profile)
   
3. Profile-specific (asset-service-dev.yaml)
   └── Applied to service with 'dev' profile
   
Priority: Profile-specific > Service-specific > Global
```

### Configuration Sources
```yaml
Config Server reads from:
  Git Repository: https://github.com/AkhilDuddela5354/test-config-server
  
  Structure:
  ├── global/application.yml
  ├── services/{service-name}/{service-name}.yaml
  └── services/{service-name}/{service-name}-{profile}.yaml
```

## 🚀 Complete Startup Commands

### Production Mode (Docker Compose)
```bash
# Clean start
docker compose down -v
docker compose build --no-cache
docker compose up -d

# Check status
docker compose ps
docker compose logs -f config-server

# Verify health
curl http://localhost:8888/actuator/health  # Config Server
curl http://localhost:8080/actuator/health  # Gateway
```

### Development Mode (Local)
```bash
# Terminal 1: Config Server (MUST START FIRST)
cd config-server
mvn clean spring-boot:run

# Wait for "Started ConfigServerApplication"

# Terminals 2-4: Services (can start in parallel)
cd asset-service && SPRING_PROFILES_ACTIVE=dev mvn spring-boot:run
cd telemetry-service && SPRING_PROFILES_ACTIVE=dev mvn spring-boot:run  
cd alert-service && SPRING_PROFILES_ACTIVE=dev mvn spring-boot:run

# Terminal 5: Gateway (start last)
cd gateway-service && mvn spring-boot:run
```

## 🔍 Monitoring & Debugging

### Health Endpoints
```bash
Config Server:    http://localhost:8888/actuator/health
Asset Service:    http://localhost:8081/actuator/health
Telemetry:        http://localhost:8082/actuator/health
Alert:            http://localhost:8083/actuator/health
Gateway:          http://localhost:8080/actuator/health
```

### Configuration Verification
```bash
# View config for a service
curl http://localhost:8888/asset-service/dev | jq

# Refresh service config
curl -X POST http://localhost:8081/actuator/refresh
```

### Docker Debugging
```bash
# View logs
docker compose logs -f [service-name]

# Execute commands in container  
docker exec -it asset-service sh

# Inspect network
docker network inspect greenko-microservices_greenko-network

# Check resource usage
docker stats
```

## 📊 Key Technical Decisions

1. **Config Server with Git Backend**: Centralized config management, version controlled
2. **H2 for Development**: Zero setup, fast iteration
3. **Docker Compose**: Single command deployment, consistent environments  
4. **Spring Cloud Gateway**: Non-blocking, reactive routing
5. **Actuator Endpoints**: Built-in monitoring and health checks
6. **Multi-stage Docker builds**: Smaller images, faster deployments
7. **Service health dependencies**: Guaranteed startup order
8. **Optional config import**: Services can start without Config Server (degraded mode)

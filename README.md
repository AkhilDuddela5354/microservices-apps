# Greenko Microservices Application

A complete Spring Boot microservices architecture featuring centralized configuration management, API Gateway, and three business services with Docker containerization.

## 🏗️ Architecture Overview

This project demonstrates a production-ready microservices architecture with the following components:

### Core Services
- **Config Server** (Port 8888) - Spring Cloud Config Server for centralized configuration management
- **Gateway Service** (Port 8080) - Spring Cloud Gateway for API routing and aggregation
- **Asset Service** (Port 8081) - Manages solar asset inventory
- **Telemetry Service** (Port 8082) - Handles real-time telemetry data
- **Alert Service** (Port 8083) - Monitors and generates alerts

### Technology Stack
- **Framework**: Spring Boot 4.0.2 / Spring Cloud 2025.1.0
- **Language**: Java 21/17
- **Build Tool**: Maven
- **Database**: H2 (in-memory) / MySQL (optional)
- **Containerization**: Docker & Docker Compose
- **API Documentation**: SpringDoc OpenAPI (Swagger)
- **Monitoring**: Spring Boot Actuator

## 📦 Project Structure

```
microservices-apps/
├── config-server/              # Spring Cloud Config Server
├── gateway-service/            # API Gateway
├── asset-service/              # Business Service 1
├── telemetry-service/          # Business Service 2
├── alert-service/              # Business Service 3
├── test-config-server/         # Git-based configuration repository
├── docker-compose.yml          # Docker orchestration
└── docs/                       # Documentation files
```

## 🚀 Quick Start

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- Docker & Docker Compose V2
- Git

### Running with Docker Compose (Recommended)

1. **Clone the repository**
   ```bash
   git clone https://github.com/AkhilDuddela5354/microservices-apps.git
   cd microservices-apps
   ```

2. **Start all services**
   ```bash
   docker compose up -d
   ```

3. **Check service health**
   ```bash
   docker compose ps
   ```

4. **View logs**
   ```bash
   docker compose logs -f [service-name]
   ```

### Running Locally (Without Docker)

1. **Start Config Server first**
   ```bash
   cd config-server
   mvn spring-boot:run
   ```

2. **Start other services** (in separate terminals)
   ```bash
   # Asset Service
   cd asset-service
   SPRING_PROFILES_ACTIVE=dev mvn spring-boot:run
   
   # Telemetry Service
   cd telemetry-service
   SPRING_PROFILES_ACTIVE=dev mvn spring-boot:run
   
   # Alert Service
   cd alert-service
   SPRING_PROFILES_ACTIVE=dev mvn spring-boot:run
   
   # Gateway Service
   cd gateway-service
   mvn spring-boot:run
   ```

## 🌐 API Access

### Gateway Endpoints (All traffic goes through gateway)
- **Gateway**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **Asset API**: http://localhost:8080/api/assets
- **Telemetry API**: http://localhost:8080/api/telemetry
- **Alert API**: http://localhost:8080/api/alerts

### API Documentation
- **Asset Service Docs**: http://localhost:8080/assets/v3/api-docs
- **Telemetry Service Docs**: http://localhost:8080/telemetry/v3/api-docs
- **Alert Service Docs**: http://localhost:8080/alerts/v3/api-docs

### Config Server
- **Config Server**: http://localhost:8888
- **Health Check**: http://localhost:8888/actuator/health
- **Get Asset Service Config**: http://localhost:8888/asset-service/dev

### Direct Service Access (Bypassing Gateway)
- **Asset Service**: http://localhost:8081
- **Telemetry Service**: http://localhost:8082
- **Alert Service**: http://localhost:8083

## 🔧 Configuration Management

### Centralized Configuration
All service configurations are managed through the Config Server, which pulls from a Git repository:
- **Git Repository**: https://github.com/AkhilDuddela5354/test-config-server

### Configuration Structure
```
test-config-server/
├── global/
│   └── application.yml         # Global configs
├── services/
│   ├── asset-service/
│   │   ├── asset-service.yaml
│   │   ├── asset-service-dev.yaml
│   │   └── asset-service-prod.yaml
│   ├── telemetry-service/
│   ├── alert-service/
│   └── gateway-service/
└── environments/
    ├── dev/
    └── prod/
```

### Refreshing Configuration
```bash
# Refresh a specific service configuration
curl -X POST http://localhost:8081/actuator/refresh

# Refresh via gateway
curl -X POST http://localhost:8080/api/assets/actuator/refresh
```

## 🐳 Docker Commands

### Basic Operations
```bash
# Build all images
docker compose build

# Start services
docker compose up -d

# Stop services
docker compose down

# View logs
docker compose logs -f

# Restart a specific service
docker compose restart gateway-service

# Rebuild and restart a service
docker compose up -d --build gateway-service
```

### Cleanup
```bash
# Stop and remove containers, networks
docker compose down

# Remove all (including volumes)
docker compose down -v

# Remove orphaned containers
docker compose down --remove-orphans
```

## 📊 Database Configuration

### Current Setup (H2 In-Memory)
Each service uses its own H2 in-memory database:
- **Asset Service**: `jdbc:h2:mem:assets`
- **Telemetry Service**: `jdbc:h2:mem:telemetry`
- **Alert Service**: `jdbc:h2:mem:alerts`

**Note**: Data is lost when containers restart.

### MySQL Setup (Optional)
To use persistent MySQL storage:

1. Uncomment the MySQL service in `docker-compose.yml` (lines 122-138)
2. Update service configurations to use MySQL profiles
3. Restart services:
   ```bash
   docker compose up -d mysql
   docker compose restart asset-service telemetry-service alert-service
   ```

## 🔍 Monitoring & Health Checks

All services expose actuator endpoints:

```bash
# Config Server Health
curl http://localhost:8888/actuator/health

# Gateway Health
curl http://localhost:8080/actuator/health

# Asset Service Health (via gateway)
curl http://localhost:8080/api/assets/actuator/health

# View available actuator endpoints
curl http://localhost:8888/actuator
```

## 📚 API Examples

### Asset Service
```bash
# Get all assets
curl http://localhost:8080/api/assets

# Get asset by ID
curl http://localhost:8080/api/assets/{id}

# Create asset
curl -X POST http://localhost:8080/api/assets \
  -H "Content-Type: application/json" \
  -d '{"name":"Solar Panel A","location":"Building 1","capacity":100.0}'
```

### Telemetry Service
```bash
# Get all telemetry data
curl http://localhost:8080/api/telemetry

# Get telemetry by asset ID
curl http://localhost:8080/api/telemetry/asset/{assetId}
```

### Alert Service
```bash
# Get all alerts
curl http://localhost:8080/api/alerts

# Get alerts by telemetry ID
curl http://localhost:8080/api/alerts/telemetry/{telemetryId}
```

## 🛠️ Development

### Building Services
```bash
# Build all services
mvn clean install

# Build specific service
cd asset-service
mvn clean package

# Skip tests
mvn clean package -DskipTests
```

### Running Tests
```bash
# Run all tests
mvn test

# Run tests for specific service
cd telemetry-service
mvn test
```

## 🔐 Security Notes

- All services communicate within the Docker network (`greenko-network`)
- Config Server uses Git-based authentication
- Health endpoints are exposed for monitoring
- Consider adding Spring Security for production deployments

## 📝 Key Features

✅ **Centralized Configuration** - Spring Cloud Config Server with Git backend  
✅ **API Gateway** - Single entry point with routing and load balancing  
✅ **Service Discovery** - Services communicate via Docker network DNS  
✅ **Health Monitoring** - Spring Boot Actuator endpoints  
✅ **API Documentation** - Swagger/OpenAPI integration  
✅ **Containerization** - Docker Compose for easy deployment  
✅ **Multi-profile Support** - Dev, Prod configurations  
✅ **Database Flexibility** - H2 for dev, MySQL for production  

## 🚨 Troubleshooting

### Services not starting
```bash
# Check logs
docker compose logs config-server
docker compose logs gateway-service

# Verify config server is healthy
curl http://localhost:8888/actuator/health
```

### Configuration not loading
```bash
# Check config server can fetch configs
curl http://localhost:8888/asset-service/dev

# Restart services to reload config
docker compose restart asset-service
```

### Port conflicts
```bash
# Check which ports are in use
docker compose ps
netstat -tuln | grep 808
```

## 📖 Documentation

Additional documentation is available in the following files:
- `CONFIG_SERVER_FINAL.md` - Detailed Config Server setup
- `DOCKER_SETUP.md` - Docker configuration guide
- `DOCKER_RUNNING.md` - Running services documentation

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is for educational and demonstration purposes.

## 👤 Author

**Akhil Duddela**
- GitHub: [@AkhilDuddela5354](https://github.com/AkhilDuddela5354)

## 🙏 Acknowledgments

- Spring Boot & Spring Cloud teams
- Docker community
- All open-source contributors

---

**Built with ❤️ using Spring Boot, Spring Cloud, and Docker**

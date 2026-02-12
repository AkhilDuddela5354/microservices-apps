# Spring Cloud Config Server - Configuration Guide

## 📋 Overview

This project uses **Spring Cloud Config Server** to manage all microservice configurations centrally. All configuration is stored in the Git repository (`test-config-server` folder) and distributed to services at runtime.

## 🏗️ Configuration Architecture

```
test-config-server/
├── global/
│   └── application.yml          # Applied to ALL services
├── services/
│   ├── asset-service/
│   │   ├── asset-service.yaml          # Base config (all profiles)
│   │   ├── asset-service-dev.yaml      # Development overrides
│   │   └── asset-service-prod.yaml     # Production overrides
│   ├── telemetry-service/
│   │   ├── telemetry-service.yaml
│   │   ├── telemetry-service-dev.yaml
│   │   └── telemetry-service-prod.yaml
│   ├── alert-service/
│   │   ├── alert-service.yaml
│   │   ├── alert-service-dev.yaml
│   │   └── alert-service-prod.yaml
│   └── gateway-service/
│       └── gateway-service.yaml        # Gateway doesn't use profiles
└── environments/
    ├── dev/
    │   └── overrides.yml
    └── prod/
        └── overrides.yml
```

## 🔄 Configuration Hierarchy

Configurations are loaded in the following order (later overrides earlier):

1. **`global/application.yml`** - Common to all services
2. **`services/{service-name}/{service-name}.yaml`** - Service-specific base config
3. **`services/{service-name}/{service-name}-{profile}.yaml`** - Profile-specific overrides

Example for `asset-service` with `dev` profile:
```
global/application.yml
  ↓ (overridden by)
services/asset-service/asset-service.yaml
  ↓ (overridden by)
services/asset-service/asset-service-dev.yaml
```

## 📝 Configuration Files Breakdown

### 1. Global Configuration (`global/application.yml`)

**Purpose:** Common settings for ALL microservices

**Contains:**
- Management endpoints (Actuator, Prometheus, health checks)
- Logging patterns and levels
- JPA common settings
- Metrics configuration

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,refresh,prometheus,metrics
  metrics:
    export:
      prometheus:
        enabled: true

logging:
  level:
    root: INFO
    com.greenko: DEBUG
```

### 2. Service Base Configuration (`{service}.yaml`)

**Purpose:** Profile-independent, service-specific settings

**Contains:**
- Application name
- Server port
- JPA configuration
- Inter-service communication URLs
- SpringDoc/Swagger configuration

**Example:** `services/asset-service/asset-service.yaml`
```yaml
server:
  port: 8080

spring:
  application:
    name: asset-service
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
```

### 3. Development Profile (`{service}-dev.yaml`)

**Purpose:** Development environment settings

**Contains:**
- H2 in-memory database configuration
- H2 console enabled
- Debug logging
- SQL initialization

**Example:** `services/asset-service/asset-service-dev.yaml`
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:assets
    username: sa
    password:
    driver-class-name: org.h2.Driver
  h2:
    console:
      enabled: true
      path: /h2-console

logging:
  level:
    com.greenko.assetservice: DEBUG
    org.hibernate.SQL: DEBUG
```

### 4. Production Profile (`{service}-prod.yaml`)

**Purpose:** Production environment settings

**Contains:**
- MySQL database configuration
- Connection pooling
- Production logging levels
- Schema validation (no auto DDL)

**Example:** `services/asset-service/asset-service-prod.yaml`
```yaml
spring:
  datasource:
    url: jdbc:mysql://${MYSQL_HOST:mysql}:${MYSQL_PORT:3306}/${MYSQL_DATABASE:assets_db}
    username: ${MYSQL_USER:root}
    password: ${MYSQL_PASSWORD:password}
    driver-class-name: com.mysql.cj.jdbc.Driver
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5

  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false

logging:
  level:
    com.greenko.assetservice: INFO
```

## 🔧 Local Service Configuration (Minimal)

Each microservice has a **minimal** local `application.yaml` with only 3 settings:

```yaml
spring:
  application:
    name: asset-service              # Service identification
  config:
    import: optional:configserver:http://config-server:8888  # Config server URL
  profiles:
    active: dev                       # Active profile (dev/prod)
```

**Why minimal?**
- ✅ All config is centralized in Git
- ✅ No need to rebuild Docker images for config changes
- ✅ Easy to manage and version control
- ✅ Follows 12-factor app methodology

## 🚀 How It Works

### 1. **Service Startup Sequence:**
```
1. Service starts → Reads local application.yaml
2. Connects to Config Server (config-server:8888)
3. Config Server fetches configs from Git repository
4. Config Server sends combined configuration back
5. Service applies configuration and starts
```

### 2. **Configuration Refresh (Runtime Updates)**

Without restarting services, you can update configurations:

```bash
# 1. Update config files in test-config-server/
# 2. Commit and push to Git
cd test-config-server
git add .
git commit -m "Update configuration"
git push

# 3. Trigger refresh on specific service
curl -X POST http://localhost:8081/actuator/refresh

# 4. Or restart config server to clear cache
docker compose restart config-server
docker compose restart asset-service
```

## 🌍 Environment-Specific Configuration

### Development Environment (Profile: `dev`)
- **Database:** H2 in-memory (auto-reset on restart)
- **Logging:** DEBUG level
- **H2 Console:** Enabled at `/h2-console`
- **SQL:** Show queries and auto-create schema

### Production Environment (Profile: `prod`)
- **Database:** MySQL (persistent)
- **Logging:** INFO/WARN level
- **Connection Pool:** Hikari with 10 connections
- **SQL:** Schema validation only (no auto-creation)

**To switch to production:**
```yaml
# In service's local application.yaml
spring:
  profiles:
    active: prod  # Change from 'dev' to 'prod'
```

## 📊 Configuration Management Best Practices

### ✅ DO:
1. **Store common configs** in `global/application.yml`
2. **Use environment variables** for sensitive data (`${MYSQL_PASSWORD}`)
3. **Version control** all configuration files
4. **Use profiles** for environment-specific settings
5. **Keep local configs minimal** (3-4 lines only)
6. **Test config changes** in dev before production

### ❌ DON'T:
1. **Don't hardcode secrets** in config files
2. **Don't duplicate configs** across services (use global)
3. **Don't put configs** in local `application.yaml`
4. **Don't skip testing** config changes
5. **Don't forget** to commit and push changes to Git

## 🔍 Verifying Configuration

### Check what config a service received:
```bash
# View all configuration for asset-service
curl http://localhost:8888/asset-service/dev | jq

# View environment info
curl http://localhost:8888/asset-service/dev | jq '.propertySources[].name'
```

### Check service logs for config loading:
```bash
docker logs asset-service 2>&1 | grep "Located environment"
```

You should see:
```
Located environment: name=application, profiles=[default], label=null, version=550deb8...
Located environment: name=asset-service, profiles=[dev], label=null, version=550deb8...
```

### Verify actuator endpoints are loaded:
```bash
curl http://localhost:8081/actuator | jq
```

## 🛠️ Adding a New Service

To add a new service to the config server:

1. **Create config directory:**
```bash
mkdir -p test-config-server/services/new-service
```

2. **Create base config** (`new-service.yaml`):
```yaml
server:
  port: 8084

spring:
  application:
    name: new-service
  jpa:
    hibernate:
      ddl-auto: update
```

3. **Create dev config** (`new-service-dev.yaml`):
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:newservice
    username: sa
    password:
```

4. **Create prod config** (`new-service-prod.yaml`):
```yaml
spring:
  datasource:
    url: jdbc:mysql://${MYSQL_HOST:mysql}:3306/newservice_db
    username: ${MYSQL_USER:root}
    password: ${MYSQL_PASSWORD:password}
```

5. **Commit and push:**
```bash
cd test-config-server
git add services/new-service/
git commit -m "Add configuration for new-service"
git push
```

6. **Create minimal local config** in the new service:
```yaml
spring:
  application:
    name: new-service
  config:
    import: optional:configserver:http://config-server:8888
  profiles:
    active: dev
```

## 📚 Config Server URLs

| Endpoint | Description |
|----------|-------------|
| `http://localhost:8888/{service}/{profile}` | Get full config for service+profile |
| `http://localhost:8888/{service}/default` | Get service config with default profile |
| `http://localhost:8888/application/default` | Get global application config |

**Examples:**
```bash
# Asset service dev config
curl http://localhost:8888/asset-service/dev

# Gateway service config
curl http://localhost:8888/gateway-service/default

# Global config
curl http://localhost:8888/application/default
```

## 🔐 Security Considerations

1. **Sensitive Data:** Use environment variables, not plain text
   ```yaml
   password: ${MYSQL_PASSWORD:defaultpass}
   ```

2. **Git Repository:** Can be private GitHub repo (current setup)

3. **Encryption:** Spring Cloud Config supports encrypted values
   ```yaml
   password: '{cipher}ENCRYPTED_VALUE'
   ```

4. **Access Control:** Restrict config server to internal network only

## 📈 Monitoring Configuration

All services expose these config-related endpoints:

- **`/actuator/env`** - View all environment properties
- **`/actuator/configprops`** - View @ConfigurationProperties beans
- **`/actuator/refresh`** - Refresh configuration without restart

## ✅ Summary

| Aspect | Implementation |
|--------|----------------|
| **Config Storage** | Git repository (`test-config-server/`) |
| **Config Server** | Spring Cloud Config Server on port 8888 |
| **Git URL** | `https://github.com/AkhilDuddela5354/microservices-apps.git` |
| **Search Paths** | `test-config-server/global`, `test-config-server/services/{application}` |
| **Profiles** | `dev` (H2), `prod` (MySQL) |
| **Local Config** | Minimal (3 lines: name, import, profile) |
| **Refresh Strategy** | Manual via `/actuator/refresh` or restart |

---

**Current Configuration Status:** ✅ All services successfully loading configuration from Config Server (Git commit: `550deb8`)

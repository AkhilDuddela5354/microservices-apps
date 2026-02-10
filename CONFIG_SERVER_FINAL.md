# Config Server Setup - Final Configuration

## ✅ Successfully Completed!

The greenko-config-server folder has been replaced with a clean Git clone of your test-config-server repository.

---

## 📁 Current Directory Structure

```
microservices-apps/
├── config-server/                    # Spring Cloud Config Server application
│   ├── src/main/resources/
│   │   └── application.yaml         # Configured with Git + Native profiles
│   └── target/
│       └── config-server-0.0.1-SNAPSHOT.jar
│
└── test-config-server/               # Cloned from GitHub (replaces greenko-config-server)
    ├── .git/                         # Git repository
    ├── global/
    │   └── application.yml
    ├── environments/
    │   ├── dev/overrides.yml
    │   └── prod/overrides.yml
    └── services/
        ├── alert-service/
        ├── asset-service/
        ├── gateway-service/
        └── telemetry-service/
```

---

## ⚙️ Config Server - Two Modes

The config-server now supports **two profiles**:

### 1. **Default (Git Mode)** - Production Use
Fetches configurations from GitHub repository remotely.

```bash
# Start with Git mode (default)
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
cd config-server
mvn spring-boot:run
```

**Configuration:**
```yaml
spring:
  cloud:
    config:
      server:
        git:
          uri: https://github.com/AkhilDuddela5354/test-config-server.git
          default-label: main
          clone-on-start: true
```

**Use when:**
- Running in production
- Want automatic updates from Git
- Multiple instances need same configs
- Want version control and audit trail

---

### 2. **Native Mode** - Development Use
Uses local filesystem for faster development iteration.

```bash
# Start with Native mode
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
cd config-server
mvn spring-boot:run -Dspring-boot.run.profiles=native
```

Or:
```bash
java -jar target/config-server-0.0.1-SNAPSHOT.jar --spring.profiles.active=native
```

**Configuration:**
```yaml
spring:
  cloud:
    config:
      server:
        native:
          search-locations:
            - file:../test-config-server/global
            - file:../test-config-server/services/{application}
            - file:../test-config-server/environments/{profile}
```

**Use when:**
- Developing locally
- Testing configuration changes before committing
- Faster iteration (no Git push/pull needed)
- Debugging configuration issues

---

## 🔄 Workflow for Configuration Updates

### Git Mode (Production)
```bash
cd test-config-server

# 1. Edit configuration files
vim services/asset-service/asset-service-dev.yaml

# 2. Commit changes
git add .
git commit -m "Update asset-service dev config"

# 3. Push to GitHub
git push origin main

# 4. Config Server automatically pulls changes on next request
# Or force refresh:
curl -X POST http://localhost:8888/actuator/refresh
```

### Native Mode (Development)
```bash
cd test-config-server

# 1. Edit configuration files
vim services/asset-service/asset-service-dev.yaml

# 2. Changes are immediately available (no commit needed)
# Just restart your microservice or call refresh endpoint

# 3. When satisfied, commit and push
git add .
git commit -m "Update asset-service dev config"
git push origin main
```

---

## 🚀 Quick Start Commands

### Start Config Server (Git Mode)
```bash
cd /home/akhilduddela/test/greenko-level-2/microservices-apps/config-server
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
mvn spring-boot:run
```

### Start Config Server (Native Mode)
```bash
cd /home/akhilduddela/test/greenko-level-2/microservices-apps/config-server
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
mvn spring-boot:run -Dspring-boot.run.profiles=native
```

### Test Configuration Endpoints
```bash
# Asset Service - dev profile
curl http://localhost:8888/asset-service/dev | jq

# Telemetry Service - prod profile
curl http://localhost:8888/telemetry-service/prod | jq

# Alert Service - dev profile
curl http://localhost:8888/alert-service/dev | jq

# Gateway Service - default
curl http://localhost:8888/gateway-service/default | jq
```

---

## 📂 Repository Information

- **GitHub URL**: https://github.com/AkhilDuddela5354/test-config-server.git
- **Local Clone**: `/home/akhilduddela/test/greenko-level-2/microservices-apps/test-config-server`
- **Branch**: main
- **SSH Key**: `~/.ssh/github_greenko`

### Update Local Clone
```bash
cd /home/akhilduddela/test/greenko-level-2/microservices-apps/test-config-server
git pull origin main
```

### Push Local Changes
```bash
cd /home/akhilduddela/test/greenko-level-2/microservices-apps/test-config-server
git add .
git commit -m "Your commit message"
git push origin main
```

---

## 🎯 What Changed

| Before | After |
|--------|-------|
| `greenko-config-server/` folder | `test-config-server/` folder |
| Not a clean Git repo | Fresh clone from GitHub |
| Only native mode | Git mode + Native mode |
| Manual folder management | Git version control |

---

## ✨ Benefits

1. **Clean Git History**: Fresh clone with proper Git tracking
2. **Dual Mode**: Choose Git (production) or Native (development)
3. **Version Control**: All changes tracked in GitHub
4. **Easy Sync**: Pull latest from GitHub anytime
5. **Rollback Support**: Revert to previous configs easily
6. **Team Collaboration**: Share configs via Git

---

## 📝 Configuration Files (13 total)

✅ **Global**: 1 file
- `global/application.yml`

✅ **Environments**: 2 files  
- `environments/dev/overrides.yml`
- `environments/prod/overrides.yml`

✅ **Services**: 10 files
- Asset Service: 3 files (base, dev, prod)
- Telemetry Service: 3 files (base, dev, prod)
- Alert Service: 3 files (base, dev, prod)
- Gateway Service: 1 file (base)

---

## 🔧 Next Steps

1. ✅ Cloned test-config-server from GitHub
2. ✅ Deleted old greenko-config-server folder
3. ✅ Updated config-server with dual-mode support
4. ✅ Build successful
5. 🔲 Start Config Server (choose Git or Native mode)
6. 🔲 Test configuration endpoints
7. 🔲 Connect microservices to Config Server
8. 🔲 Test both Git and Native modes

---

## 🐛 Troubleshooting

**Issue**: Config Server can't find configuration files in native mode  
**Solution**: Verify path: `../test-config-server` is correct relative to config-server directory

**Issue**: Git mode fails to clone repository  
**Solution**: Check internet connection and GitHub URL

**Issue**: Changes not reflecting  
**Solution**: 
- Git mode: Make sure you pushed to GitHub
- Native mode: Check file was saved in test-config-server folder
- Call refresh endpoint on microservice

---

**Status**: ✅ Ready! Choose your mode and start the Config Server.

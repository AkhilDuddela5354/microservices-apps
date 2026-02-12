# Monitoring Setup - Prometheus, cAdvisor & Grafana

## 🎯 What's Included

This setup provides complete observability for your microservices:

1. **Prometheus** - Metrics collection and storage
2. **cAdvisor** - Container resource monitoring  
3. **Grafana** - Beautiful dashboards and visualization

## 📊 Access URLs

Once docker compose is running:

| Service | URL | Credentials |
|---------|-----|-------------|
| Prometheus | http://localhost:9090 | No auth |
| cAdvisor | http://localhost:8085 | No auth |
| Grafana | http://localhost:3000 | admin/admin |

## 🚀 Quick Start

```bash
# Start all services including monitoring stack
docker compose up -d

# Check all services are running
docker compose ps

# View Prometheus targets
open http://localhost:9090/targets

# View container metrics in cAdvisor
open http://localhost:8085

# Access Grafana dashboards
open http://localhost:3000
```

## 📈 What Metrics Are Collected

### Application Metrics (via Actuator + Micrometer)
- ✅ HTTP request count, duration, status codes
- ✅ JVM memory usage (heap, non-heap)
- ✅ Garbage collection statistics
- ✅ Thread count and states
- ✅ Database connection pool metrics
- ✅ Custom business metrics

### Container Metrics (via cAdvisor)
- ✅ CPU usage per container
- ✅ Memory usage and limits
- ✅ Network I/O (bytes in/out)
- ✅ Disk I/O operations
- ✅ Container lifecycle events

## 🔍 Useful Prometheus Queries

```promql
# Request rate per service
rate(http_server_requests_seconds_count[5m])

# Average response time
rate(http_server_requests_seconds_sum[5m]) / rate(http_server_requests_seconds_count[5m])

# JVM memory used
jvm_memory_used_bytes{area="heap"}

# CPU usage
system_cpu_usage

# Container CPU usage (from cAdvisor)
rate(container_cpu_usage_seconds_total[5m])

# Container memory usage
container_memory_usage_bytes
```

## 📊 Grafana Dashboard Setup

1. Login to Grafana: http://localhost:3000 (admin/admin)
2. Data source already configured (Prometheus)
3. Import popular dashboards:
   - Spring Boot Statistics: Dashboard ID `6756`
   - JVM (Micrometer): Dashboard ID `4701`
   - Docker Container Metrics: Dashboard ID `193`

**To import:**
- Click "+" → "Import"
- Enter Dashboard ID
- Select "Prometheus" as data source
- Click "Import"

## 🔧 Configuration Files

### prometheus.yml
Defines what services to scrape:
- Spring Boot services at `/actuator/prometheus`
- cAdvisor at port 8080
- Prometheus itself

### grafana-datasource.yml
Auto-configures Prometheus as Grafana data source

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────┐
│                    Grafana (3000)                    │
│              Visualization & Dashboards              │
└────────────────────┬────────────────────────────────┘
                     │ Queries metrics
                     ▼
┌─────────────────────────────────────────────────────┐
│                 Prometheus (9090)                    │
│          Time-Series Database & Scraper             │
└──────────┬──────────────────────┬───────────────────┘
           │                      │
           │ Scrapes every 15s    │ Scrapes every 15s
           ▼                      ▼
┌──────────────────────┐   ┌──────────────────────┐
│   Spring Services    │   │   cAdvisor (8085)    │
│  /actuator/prometheus│   │  Container Metrics   │
│                      │   │                      │
│ - config-server      │   │  Monitors:           │
│ - asset-service      │   │  - asset-service     │
│ - telemetry-service  │   │  - gateway-service   │
│ - alert-service      │   │  - config-server     │
│ - gateway-service    │   │  - prometheus        │
└──────────────────────┘   └──────────────────────┘
```

## 🛠️ Troubleshooting

### Prometheus not scraping services?

```bash
# Check Prometheus targets status
curl http://localhost:9090/api/v1/targets | jq

# Verify service exposes prometheus endpoint
curl http://localhost:8081/actuator/prometheus

# Check Prometheus logs
docker compose logs prometheus
```

### cAdvisor not showing containers?

```bash
# Verify cAdvisor is running
docker compose ps cadvisor

# Check cAdvisor logs
docker compose logs cadvisor

# Access cAdvisor UI
open http://localhost:8085/containers/
```

### Grafana can't connect to Prometheus?

```bash
# Verify they're on same network
docker network inspect greenko-microservices_greenko-network

# Test connectivity from Grafana container
docker exec grafana wget -O- http://prometheus:9090
```

## 📝 Key Dependencies Added

All microservices now include:

```xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

This automatically:
- Exposes `/actuator/prometheus` endpoint
- Formats metrics in Prometheus format
- Includes JVM, HTTP, and system metrics

## 🔐 Production Considerations

For production deployments:

1. **Add authentication** to Prometheus and Grafana
2. **Configure retention** policies for metrics
3. **Set up alerting** rules in Prometheus
4. **Enable HTTPS** for all endpoints
5. **Configure backups** for Grafana dashboards
6. **Adjust scrape intervals** based on load
7. **Set resource limits** for monitoring containers

## 📚 Learn More

- [Prometheus Documentation](https://prometheus.io/docs)
- [cAdvisor GitHub](https://github.com/google/cadvisor)
- [Grafana Documentation](https://grafana.com/docs)
- [Micrometer Documentation](https://micrometer.io/docs)

---

**Happy Monitoring!** 📊🎉

# TurboTax Microservices

A multi-service architecture for TurboTax applications with Java Spring Boot services and a Python FastAPI AI-powered agent UI.

## Services Overview

- **TurboTax Filing Query Service** (Java/Spring Boot) - Port 7001
- **TurboTax Refund Aggregation Service** (Java/Spring Boot) - Port 7002
- **TurboTax Refund Query Service** (Java/Spring Boot) - Port 8001
- **TurboTax Agent UI** (Python/FastAPI) - Port 8080 - AI-powered tax assistance and web interface

## Recent Changes (November 23, 2025)
- **Multi-Root Workspace Setup**: Configured VS Code workspace with independent environments for each service
- **AI Provider Configuration**: Fixed dynamic provider availability in Agent UI service
- **Service Restructuring**: Streamlined to 4 core services with clear separation of concerns
- **Workspace Documentation**: Updated README and workspace configuration for current architecture

## Quick Start
```bash
# Build all Java services
./gradlew build

# Test all services
./gradlew test

# Start all Java services
./gradlew startAllServices

# Start Python Agent UI service
cd turbotax-agent-ui && ./venv/bin/uvicorn turbotax.agent_ui.main:app --host 0.0.0.0 --port 8080
```

## API Endpoints

### Filing Query Service (Port 7001)
```bash
curl -H "X-USER-ID: user123" localhost:7001/filings -s | jq .
curl -H "X-USER-ID: user123" localhost:7001/filings/202410001 -s | jq .
```

### Refund Aggregation Service (Port 7002)
```bash
curl -H "X-USER-ID: user123" localhost:7002/aggregate-status/202410001 -s | jq .
```

### Refund Query Service (Port 8001)
```bash
curl -H "X-USER-ID: user123" localhost:8001/refund-status -s | jq .
```

### Agent UI Service (Port 8080)
```bash
# Health check
curl localhost:8080/api/health

# AI assistance query
curl -X POST "localhost:8080/api/assist" \
  -H "Content-Type: application/json" \
  -d '{"user_id": "user123", "query": "What is my refund status?", "provider": "ollama"}'
``` 

## Architecture Overview

The system follows an event-driven microservices architecture:

1. **Filing Query Service** - Handles tax filing data queries
2. **Refund Aggregation Service** - Aggregates refund status from multiple sources
3. **Refund Query Service** - Provides unified refund status API
4. **Agent UI Service** - AI-powered web interface for tax assistance

## TODO
* Implement event publishing for tax-return-filed-event
* Connect filing events to refund aggregation service
* Add refund status update event publishing
* Implement customer notification service
* Add model training pipeline for AI improvements

<details>
<summary>Gradle Service Tasks</summary>

## Individual Service Tasks

### Start Individual Services (Background)
```bash
./gradlew startFilingQueryService      # Port 7001
./gradlew startRefundAggregationService # Port 7002
./gradlew startRefundQueryService      # Port 8001

# Python Agent UI (manual start)
cd turbotax-agent-ui && PYTHONPATH=src/main/python ./venv/bin/uvicorn turbotax.agent_ui.main:app --host 0.0.0.0 --port 8080
```

### Service URLs
- Filing Query: http://localhost:7001
- Refund Aggregation: http://localhost:7002
- Refund Query: http://localhost:8001
- Agent UI: http://localhost:8080

### Service Logs
All Java service logs are stored in the `logs/` directory:
- Filing Query: `logs/filing-query-service.log`
- Refund Aggregation: `logs/refund-aggregation-service.log`
- Refund Query: `logs/refund-query-service.log`

Agent UI logs are displayed in the terminal when running.

</details>
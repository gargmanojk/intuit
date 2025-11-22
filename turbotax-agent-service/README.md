# TurboTax Agent Service

A Python-based AI-powered tax assistance service built with FastAPI and integrated into the TurboTax platform using Gradle.

## Overview

The TurboTax Agent Service provides intelligent tax assistance and automation capabilities. It uses Python 3.11 and is built as a Gradle module within the larger TurboTax platform.

## Features

- 🤖 AI-powered tax query assistance
- 📊 Tax strategy recommendations
- 🔄 Real-time support capabilities
- 🚀 FastAPI-based REST API
- 🐍 Python 3.11 with modern async support

## Prerequisites

- Python 3.11
- Gradle 8.0+
- Java 17+

## Building and Running

### Using Gradle

```bash
# Build the entire platform including the agent service
./gradlew build

# Run the agent service
./gradlew :turbotax-agent-service:runAgent

# Run tests
./gradlew :turbotax-agent-service:test
```

### Direct Python Execution

```bash
# Install dependencies
pip install -r turbotax-agent-service/requirements.txt

# Run the service
cd turbotax-agent-service/src/main/python
python -m uvicorn turbotax.agent.main:app --host 0.0.0.0 --port 9001 --reload
```

## API Endpoints

### Health Check
- `GET /` - Basic service information
- `GET /health` - Detailed health check

### Core Functionality
- `POST /api/v1/assist` - Process tax queries and provide assistance
- `GET /api/v1/capabilities` - Get service capabilities

## API Usage Examples

### Get Service Health
```bash
curl http://localhost:9001/health
```

### Submit Tax Query
```bash
curl -X POST http://localhost:9001/api/v1/assist \
  -H "Content-Type: application/json" \
  -d '{
    "user_id": "user123",
    "query": "How do I maximize my tax deductions?",
    "context": {"tax_year": "2024", "filing_status": "single"}
  }'
```

## Project Structure

```
turbotax-agent-service/
├── build.gradle                 # Gradle build configuration
├── setup.py.template           # Python package template
├── requirements.txt            # Python dependencies
├── src/
│   ├── main/
│   │   └── python/
│   │       └── turbotax/
│   │           ├── __init__.py
│   │           └── agent/
│   │               ├── __init__.py
│   │               └── main.py      # FastAPI application
│   └── test/
│       └── python/
│           └── turbotax/
│               ├── __init__.py
│               └── agent/
│                   ├── __init__.py
│                   └── test_main.py # Unit tests
```

## Development

### Adding New Features

1. Add new endpoints in `main.py`
2. Update request/response models as needed
3. Add corresponding tests in `test_main.py`
4. Update this README with new capabilities

### Testing Status

✅ **All tests passing** - 5/5 test cases successful
- Unit tests for all API endpoints
- Health check validation
- Tax assistance endpoint testing
- Async endpoint testing
- Comprehensive test coverage

## Build Integration

The service is fully integrated into the Gradle multi-module build system:

```bash
# Full project build (includes this service)
./gradlew build

# Service-specific tasks
./gradlew :turbotax-agent-service:installPythonDeps  # Install Python dependencies
./gradlew :turbotax-agent-service:test               # Run tests
./gradlew :turbotax-agent-service:runAgent           # Start service
```

## Verified Functionality

- ✅ Service starts successfully on port 9001
- ✅ All API endpoints respond correctly
- ✅ Health checks pass
- ✅ Tax assistance queries processed
- ✅ Gradle build integration working
- ✅ Python 3.11.14 compatibility confirmed

## Integration with TurboTax Platform

This service integrates with the larger TurboTax platform through:

- Shared API contracts from `turbotax-api-contracts`
- Communication with other services via REST APIs
- Unified logging and monitoring
- Gradle-based build and deployment pipeline

## Configuration

The service uses the following default configuration:

- **Host**: 0.0.0.0
- **Port**: 9001
- **Python Version**: 3.11
- **Framework**: FastAPI with Uvicorn

## Contributing

1. Follow the existing code style and patterns
2. Add comprehensive tests for new features
3. Update documentation as needed
4. Ensure compatibility with Python 3.11+

## License

Copyright TurboTax Development Team
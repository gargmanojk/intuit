# TurboTax Agent Service

A FastAPI-based microservice that provides AI-powered tax assistance through multiple providers (Ollama, OpenAI). This service integrates with the TurboTax refund query system to provide comprehensive tax support with real-time streaming responses.

## Architecture

This service follows a modular architecture with clear separation of concerns:
- **Core Layer**: AI assistants, data models, and prompt engineering
- **Service Layer**: Business logic for query processing and refund integration
- **Infrastructure Layer**: Dependencies, configuration, and external service clients
- **Interface Layer**: Abstract contracts for AI providers and services

## Features

- **Multi-Provider AI Support**: Ollama (local, free) and OpenAI (cloud, API costs)
- **Real-time Streaming**: Server-Sent Events (SSE) for live AI responses
- **Refund Integration**: Automatic refund status checking and AI-enhanced responses
- **Async Processing**: Non-blocking I/O with FastAPI and async/await patterns
- **Health Monitoring**: Comprehensive health checks and service status
- **Environment Management**: Flexible configuration for different deployment environments

## Prerequisites

- Python 3.9+
- Ollama (for local AI) or OpenAI API key (for cloud AI)
- Access to dependent services:
  - Refund Query Service (port 7000)

## Installation

### Development Setup

1. **Clone and navigate**:
   ```bash
   git clone <repository>
   cd turbotax-agent-service
   ```

2. **Create virtual environment**:
   ```bash
   python3 -m venv venv
   source venv/bin/activate  # On Windows: venv\Scripts\activate
   ```

3. **Install dependencies**:
   ```bash
   pip install -e ".[dev]"
   ```

4. **Configure environment**:
   ```bash
   cp config/development.env .env
   # Edit .env with your settings
   ```

### Production Setup

```bash
pip install turbotax-agent-service
```

## Configuration

### Environment Variables

Create a `.env` file or set environment variables:

```bash
# AI Service Configuration
OPENAI_API_KEY=sk-proj-...  # Get from https://platform.openai.com/api-keys
OLLAMA_BASE_URL=http://localhost:11434

# Default AI provider (ollama or openai)
AI_PROVIDER=ollama

# Model configurations
OPENAI_MODEL=gpt-4o-mini
OLLAMA_MODEL=llama2

# Agent Service Configuration
AGENT_SERVICE_PORT=8001
AGENT_SERVICE_HOST=0.0.0.0

# Refund Service Configuration
REFUND_SERVICE_URL=http://localhost:7000/api/v1/refund-status

# Logging Configuration
LOG_LEVEL=INFO
LOG_BASE_PATH=/tmp
SERVICE_NAME=turbotax-agent-service
```

### Configuration Files

Pre-configured environment files are available in `config/`:
- `development.env` - Development settings
- `production.env` - Production settings
- `test.env` - Test environment settings

## API Documentation

### Base URL
```
http://localhost:8001
```

### Health Check

**Endpoint:** `GET /health`

**Response:**
```json
{
  "status": "healthy",
  "service": "turbotax-agent-service",
  "version": "1.0.0",
  "assistants": ["ollama", "openai"]
}
```

### AI Assistance

**Endpoint:** `POST /api/assist`

**Headers:**
- `Content-Type: application/json`

**Request Body:**
```json
{
  "user_id": "user123",
  "query": "What is my refund status?",
  "provider": "ollama",
  "stream": false,
  "context": {
    "tax_year": "2024"
  }
}
```

**Response (Non-streaming):**
```json
{
  "response": "Based on your refund information, your tax return for 2024 is currently being processed...",
  "confidence": 0.85,
  "suggestions": [
    "Consider consulting a tax professional for complex situations",
    "Keep all tax-related documents organized",
    "Review your tax return before filing"
  ],
  "next_steps": [
    "Gather all necessary tax documents",
    "Review your tax situation with a professional",
    "File your taxes by the deadline"
  ]
}
```

**Streaming Response:**
Returns Server-Sent Events (SSE) with real-time chunks:
```
data: Based
data:  on
data:  your
data:  refund
...
data: [DONE]
```

## Running the Service

### Development Mode

```bash
# Using Gradle (from root)
./gradlew startAgentService

# Or directly with Python
python -m turbotax.agent_service.main
```

### Production Mode

```bash
# Using uvicorn directly
uvicorn turbotax.agent_service.main:app --host 0.0.0.0 --port 8001

# Or as a systemd service
# Configure systemd service file pointing to your virtual environment
```

### Using Gradle Tasks (from root)

```bash
# Start service in background
./gradlew startAgentService

# Check service status
./gradlew statusAgentService

# Stop service
./gradlew stopAgentService

# Restart service
./gradlew restartAgentService
```

## Testing

### Unit Tests

Run the test suite:
```bash
pytest tests/
```

### Integration Tests

Test with real AI providers:
```bash
# Test Ollama integration
pytest tests/ -k ollama

# Test OpenAI integration
pytest tests/ -k openai
```

### API Testing

Use the provided `requests.http` file with VS Code REST Client or curl:

```bash
# Health check
curl http://localhost:8001/health

# Non-streaming request
curl -X POST http://localhost:8001/api/assist \
  -H "Content-Type: application/json" \
  -d '{"user_id": "test", "query": "Hello", "provider": "ollama", "stream": false}'

# Streaming request
curl -N http://localhost:8001/api/assist \
  -H "Content-Type: application/json" \
  -d '{"user_id": "test", "query": "Hello", "provider": "ollama", "stream": true}'
```

## AI Providers

### Ollama (Recommended for Development)

**Pros:**
- Runs locally, no API costs
- Fast response times
- Privacy-focused (data stays local)

**Setup:**
```bash
# Install Ollama
curl -fsSL https://ollama.ai/install.sh | sh

# Pull the model
ollama pull llama2

# Start Ollama service
ollama serve
```

### OpenAI (Production/Advanced Features)

**Pros:**
- More accurate responses
- Access to latest GPT models
- Better handling of complex queries

**Setup:**
- Get API key from [OpenAI Platform](https://platform.openai.com/api-keys)
- Set `OPENAI_API_KEY` in your `.env` file
- Monitor API usage and costs

## Project Structure

```
turbotax-agent-service/
├── src/main/python/turbotax/agent_service/
│   ├── __init__.py
│   ├── main.py                    # FastAPI application entry point
│   ├── config.py                  # Configuration and logging setup
│   ├── constants.py               # Service constants and URLs
│   ├── core/
│   │   ├── __init__.py
│   │   ├── assistants/            # AI provider implementations
│   │   │   ├── __init__.py
│   │   │   ├── base_assistant.py
│   │   │   ├── ollama_assistant.py
│   │   │   └── openai_assistant.py
│   │   ├── models.py              # Pydantic data models
│   │   └── prompts.py             # AI prompt templates
│   ├── services/
│   │   ├── __init__.py
│   │   ├── query_processor.py     # Main query processing logic
│   │   ├── refund_service.py      # Refund status integration
│   │   └── streaming_service.py   # SSE streaming implementation
│   ├── infrastructure/
│   │   ├── __init__.py
│   │   ├── dependencies.py        # Dependency injection
│   │   └── exceptions.py          # Custom exceptions
│   └── interfaces/
│       └── __init__.py            # Abstract interfaces
├── config/                        # Environment configuration files
│   ├── development.env
│   ├── production.env
│   └── test.env
├── tests/                         # Unit and integration tests
├── pyproject.toml                 # Python package configuration
├── requirements.txt               # Python dependencies
├── .env                           # Local environment variables
└── README.md
```

## Dependencies

### Core Dependencies

- **FastAPI**: Modern async web framework
- **Uvicorn**: ASGI server for FastAPI
- **LangChain**: LLM integration framework
- **Pydantic**: Data validation and serialization
- **python-dotenv**: Environment variable management
- **httpx**: Async HTTP client for external APIs

### Development Dependencies

- **pytest**: Testing framework
- **pytest-asyncio**: Async testing support
- **black**: Code formatting
- **isort**: Import sorting
- **mypy**: Type checking

## Monitoring and Logging

### Logging

Logs are configured with multiple levels:
- **Console**: Real-time output with configurable levels
- **File**: Persistent logging to `/tmp/logs/turbotax-agent-service.log`

Configure log levels:
```bash
LOG_LEVEL=DEBUG  # Options: DEBUG, INFO, WARNING, ERROR
```

### Health Checks

The service provides comprehensive health monitoring:
- AI provider availability
- External service connectivity
- Memory and performance metrics

### Metrics

Track API usage, response times, and error rates through the health endpoint.

## Troubleshooting

### Common Issues

1. **AI Provider Not Available**
   ```
   Error: Assistant error: Provider 'ollama' not available
   ```
   **Solution:** Ensure Ollama is running (`ollama serve`) or check OpenAI API key

2. **Refund Service Connection Failed**
   ```
   Error: Refund service error: 404
   ```
   **Solution:** Verify Refund Query Service is running on port 7000

3. **Port Already in Use**
   ```
   Error: [Errno 48] Address already in use
   ```
   **Solution:** Change `AGENT_SERVICE_PORT` or stop other services using port 8001

4. **Streaming Timeout**
   ```
   curl: (28) Operation timed out
   ```
   **Solution:** Complex queries take time; increase timeout or use simpler queries for testing

### Debug Mode

Enable debug logging:
```bash
LOG_LEVEL=DEBUG
```

Check service logs:
```bash
tail -f /tmp/logs/turbotax-agent-service.log
```

## Development

### Adding New AI Providers

1. Create new assistant in `core/assistants/`
2. Implement the `TaxAssistantInterface`
3. Register in `infrastructure/dependencies.py`
4. Add configuration in `.env`

### Extending Functionality

- **New Query Types**: Add detection logic in `query_processor.py`
- **External Integrations**: Add new services in `infrastructure/`
- **Custom Prompts**: Modify templates in `core/prompts.py`

### Code Quality

```bash
# Format code
black src/ tests/

# Sort imports
isort src/ tests/

# Type checking
mypy src/

# Run tests with coverage
pytest --cov=src/
```

## Deployment

### Docker (Recommended)

```dockerfile
FROM python:3.11-slim

WORKDIR /app
COPY requirements.txt .
RUN pip install -r requirements.txt

COPY . .
EXPOSE 8001

CMD ["uvicorn", "turbotax.agent_service.main:app", "--host", "0.0.0.0", "--port", "8001"]
```

### Kubernetes

Use the provided Helm charts or Kubernetes manifests for container orchestration.

## Contributing

1. Follow the existing code structure and async patterns
2. Add comprehensive tests for new features
3. Update this README for API changes
4. Ensure all tests pass before submitting PRs

## License

This project is part of the TurboTax microservices suite.
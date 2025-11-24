# TurboTax Agent UI

A modern, AI-powered web interface for TurboTax tax assistance, built with FastAPI and designed for seamless integration with the TurboTax Agent Service.

## 🚀 Overview

The TurboTax Agent UI provides a user-friendly web interface and REST API for accessing AI-powered tax assistance. It serves as the primary interface between users and the TurboTax Agent Service, offering both traditional web UI and programmatic API access.

## ✨ Features

- **Web Interface**: Clean, responsive single-page application for tax assistance
- **REST API**: Comprehensive API endpoints for programmatic access
- **Streaming Support**: Real-time streaming responses for enhanced user experience
- **Multi-Provider Support**: Integration with Ollama and OpenAI providers
- **Health Monitoring**: Built-in health checks and service monitoring
- **Modern Architecture**: FastAPI-based with async/await patterns
- **Production Ready**: Configurable for development and production environments

## 🏗️ Architecture

```
┌─────────────────┐    HTTP     ┌──────────────────┐
│   Web Browser   │────────────▶│  Agent UI        │
│                 │             │  (Port 8000)     │
└─────────────────┘             └──────────────────┘
                                   │
                                   │ HTTP Proxy
                                   ▼
                         ┌──────────────────┐
                         │  Agent Service   │
                         │  (Port 8001)     │
                         └──────────────────┘
```

## 📋 Prerequisites

- **Python**: 3.11 or higher
- **Agent Service**: TurboTax Agent Service running on port 8001
- **Virtual Environment**: Python venv (recommended)

## 🛠️ Installation

### 1. Clone and Setup
```bash
cd turbotax-agent-ui

# Create virtual environment
python3 -m venv venv
source venv/bin/activate  # On Windows: venv\Scripts\activate

# Install dependencies
pip install -e .
```

### 2. Environment Configuration
Create a `.env` file (optional):
```bash
# Agent Service URL (optional, defaults to http://localhost:8001)
AGENT_SERVICE_URL=http://localhost:8001
```

## 🚀 Running the Application

### Development Mode
```bash
# Using the script entry point
turbotax-agent-ui

# Or directly with uvicorn
PYTHONPATH=src/main/python uvicorn turbotax.agent_ui.main:app --host 0.0.0.0 --port 8000 --reload
```

### Production Mode
```bash
# Using uvicorn directly (recommended for production)
PYTHONPATH=src/main/python uvicorn turbotax.agent_ui.main:app --host 0.0.0.0 --port 8000 --workers 4
```

### Using Gradle (from project root)
```bash
# Development mode with auto-reload
./gradlew runAgentUIService

# Background production mode
./gradlew startAgentUIService
```

## 📚 API Documentation

### Base URL
```
http://localhost:8000
```

### Endpoints

#### Web Interface
- `GET /web` - Main web application interface
- `GET /` - Basic health check
- `GET /health` - Detailed health information

#### API Endpoints

##### Health & Status
- `GET /api/health` - Service health check
- `GET /api/v1/health` - Versioned health endpoint

##### Tax Assistance
- `POST /api/chat` - Chat-based tax assistance (web UI)
- `POST /api/v1/assist` - Programmatic tax assistance API
- `GET /api/v1/stream/{user_id}?query={query}&provider={provider}` - Streaming tax assistance

### API Examples

#### Basic Tax Query
```bash
curl -X POST "http://localhost:8000/api/chat" \
  -H "Content-Type: application/json" \
  -d '{
    "user_id": "user123",
    "query": "What is my refund status?",
    "provider": "ollama"
  }'
```

#### Streaming Response
```bash
curl -X POST "http://localhost:8000/api/v1/assist" \
  -H "Content-Type: application/json" \
  -d '{
    "user_id": "user123",
    "query": "Help me understand tax deductions",
    "provider": "ollama",
    "stream": true
  }'
```

#### Health Check
```bash
curl http://localhost:8000/health
# Response: {"status": "healthy", "service": "turbotax-agent-ui", "python_version": "3.11.x"}
```

## 🧪 Testing

### Run Tests
```bash
# All tests
pytest

# With coverage
pytest --cov=turbotax.agent_ui

# Specific test file
pytest src/test/python/turbotax/agent_ui/test_web_ui.py
```

### Test Structure
```
src/test/python/turbotax/agent_ui/
├── conftest.py          # Test configuration and fixtures
├── test_web_ui.py       # Web interface tests
└── test_assist.py       # API endpoint tests
```

## 🛠️ Development

### Code Quality
```bash
# Format code
black src/main/python src/test/python

# Sort imports
isort src/main/python src/test/python

# Type checking
mypy src/main/python
```

### Project Structure
```
turbotax-agent-ui/
├── src/main/python/turbotax/agent_ui/
│   ├── main.py              # Application entry point
│   ├── config.py            # Configuration and logging
│   ├── constants.py         # Application constants
│   ├── dependencies.py      # FastAPI dependencies
│   ├── models.py            # Pydantic models
│   ├── routers/
│   │   ├── assist.py        # Tax assistance endpoints
│   │   └── health.py        # Health check endpoints
│   ├── static/              # Static assets (CSS, JS)
│   └── templates/           # Jinja2 templates
├── src/test/python/turbotax/agent_ui/
│   └── ...                  # Test files
├── pyproject.toml           # Project configuration
├── requirements.txt         # Dependencies
└── README.md               # This file
```

## ⚙️ Configuration

### Environment Variables
- `AGENT_SERVICE_URL`: URL of the TurboTax Agent Service (default: `http://localhost:8001`)
- `PYTHONPATH`: Python module search path (automatically set by Gradle tasks)

### Service Dependencies
- **TurboTax Agent Service**: Must be running on port 8001
- **Database**: No direct database connections (proxies to Agent Service)

## 🔍 Monitoring & Debugging

### Logs
```bash
# View application logs
tail -f logs/turbotax-agent-ui.log

# View error logs
tail -f logs/turbotax-agent-ui-error.log
```

### Health Checks
```bash
# Service health
curl http://localhost:8000/health

# API health
curl http://localhost:8000/api/health

# Agent service connectivity
curl http://localhost:8001/health
```

### Debugging
```bash
# Enable debug logging
export PYTHONPATH=src/main/python
python -c "
import logging
logging.basicConfig(level=logging.DEBUG)
from turbotax.agent_ui.main import app
"
```

## 🚀 Deployment

### Docker (Future)
```dockerfile
FROM python:3.11-slim

WORKDIR /app
COPY . .

RUN pip install -e .
EXPOSE 8000

CMD ["turbotax-agent-ui"]
```

### Systemd Service (Linux)
```ini
[Unit]
Description=TurboTax Agent UI
After=network.target

[Service]
Type=simple
User=turbotax
WorkingDirectory=/opt/turbotax-agent-ui
Environment=PYTHONPATH=src/main/python
ExecStart=/opt/turbotax-agent-ui/venv/bin/uvicorn turbotax.agent_ui.main:app --host 0.0.0.0 --port 8000
Restart=always

[Install]
WantedBy=multi-user.target
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/amazing-feature`
3. Make your changes and add tests
4. Run the test suite: `pytest`
5. Format code: `black . && isort .`
6. Commit your changes: `git commit -m 'Add amazing feature'`
7. Push to the branch: `git push origin feature/amazing-feature`
8. Open a Pull Request

## 📝 License

This project is part of the TurboTax microservices suite. See the main project license for details.

## 🆘 Troubleshooting

### Common Issues

**"Module not found" errors**
```bash
# Ensure PYTHONPATH is set
export PYTHONPATH=src/main/python
```

**"Connection refused" to Agent Service**
```bash
# Check if Agent Service is running
curl http://localhost:8001/health

# Start Agent Service if needed
./gradlew startAgentService
```

**Port already in use**
```bash
# Kill process using port 8000
lsof -ti:8000 | xargs kill

# Or use a different port
uvicorn turbotax.agent_ui.main:app --port 8001
```

**Import errors in tests**
```bash
# Run tests from project root
cd turbotax-agent-ui
pytest
```

## 📞 Support

For support and questions:
- Check the main project README
- Review API documentation at `http://localhost:8000/docs`
- Check service logs in `logs/` directory
- Open an issue in the project repository

## 🔄 Version History

- **v1.0.0**: Initial release with FastAPI, web UI, and Agent Service integration
- Modern Python packaging with pyproject.toml
- Comprehensive testing and development tooling
- Production-ready configuration and deployment options</content>
<parameter name="filePath">/home/mgarg/projects/intuit/turbotax-agent-ui/README.md
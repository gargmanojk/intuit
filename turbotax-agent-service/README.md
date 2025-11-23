# TurboTax Agent Service

Core AI service logic for TurboTax Agent, providing tax assistance through various AI providers.

## Features

- Multiple AI provider support (Ollama, OpenAI)
- Streaming responses for real-time interaction
- Refund status checking
- Configurable logging and environment management

## Installation

### Development
```bash
git clone <repository>
cd turbotax-agent-service
pip install -e ".[dev]"
```

### Production
```bash
pip install turbotax-agent-service
```

## Configuration

Copy the appropriate environment file:

```bash
cp config/development.env .env
# or
cp config/production.env .env
```

Edit `.env` with your settings.

## Usage

### As a library
```python
from turbotax.agent_service import get_query_processor

processor = get_query_processor()
result = await processor.process_query(tax_query)
```

### As a standalone service
```bash
turbotax-agent-service
```

## Development

```bash
# Run tests
pytest

# Run with auto-reload
pip install -e ".[dev]"
turbotax-agent-service
```

## Project Structure

```
turbotax-agent-service/
├── src/turbotax/agent_service/
│   ├── core/
│   │   ├── assistants/     # AI provider implementations
│   │   ├── models.py       # Data models
│   │   └── prompts.py      # Prompt templates
│   ├── services/           # Business logic services
│   ├── infrastructure/     # Dependencies and exceptions
│   ├── interfaces/         # Abstract interfaces
│   └── main.py            # Standalone service entry point
├── config/                 # Environment configurations
├── tests/                  # Unit tests
└── pyproject.toml         # Package configuration
```
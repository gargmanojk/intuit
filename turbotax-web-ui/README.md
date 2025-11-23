# TurboTax Web UI

A modern single-page application (SPA) for TurboTax customer tax inquiries.

## Overview

The TurboTax Web UI provides a user-friendly interface for customers to interact with the TurboTax AI assistant. It features:

- **Modern SPA Design**: Clean, responsive interface built with vanilla JavaScript
- **Real-time Chat**: Interactive conversation with the tax assistant
- **Multiple AI Providers**: Support for both Ollama (local) and OpenAI (cloud) providers
- **Streaming Responses**: Optional real-time response streaming
- **Quick Actions**: Pre-defined common tax questions
- **Connection Monitoring**: Real-time service health monitoring

## Features

### User Interface
- Welcome screen with feature overview
- Seamless chat interface
- User ID management for personalized assistance
- Provider selection (Ollama/OpenAI)
- Streaming toggle for real-time responses

### Chat Features
- Message history with timestamps
- Typing indicators
- Confidence scores for AI responses
- Quick action buttons for common queries
- Error handling and retry mechanisms

### Technical Features
- Responsive design for mobile and desktop
- Progressive Web App capabilities
- RESTful API integration
- Connection health monitoring
- Error modal with retry options

## API Integration

The Web UI communicates with the TurboTax Agent Service via REST API:

- `POST /api/chat` - Send chat messages
- `GET /api/health` - Check service health

## Development

### Prerequisites
- Python 3.11+
- FastAPI
- httpx
- uvicorn

### Installation
```bash
pip install -r requirements.txt
```

### Running
```bash
python -m turbotax.web_ui.main
```

The application will be available at `http://localhost:8000`

### Configuration
Set the agent service URL via environment variable:
```bash
export AGENT_SERVICE_URL=http://localhost:9001
```

## Architecture

```
turbotax-web-ui/
├── src/main/python/turbotax/web_ui/
│   ├── main.py              # FastAPI application
│   ├── templates/
│   │   └── index.html       # Single page application
│   └── static/
│       ├── css/styles.css   # Application styles
│       └── js/app.js        # SPA JavaScript
├── requirements.txt         # Python dependencies
└── README.md               # This file
```

## Browser Support

- Chrome 90+
- Firefox 88+
- Safari 14+
- Edge 90+

## Contributing

1. Follow the existing code style
2. Add tests for new features
3. Update documentation
4. Ensure responsive design works on mobile

## License

Copyright 2025 TurboTax. All rights reserved.
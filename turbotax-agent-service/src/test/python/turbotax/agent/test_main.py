"""
Tests for TurboTax Agent Service
"""

import sys
import os

# Add the main source path to sys.path
main_path = "/home/mgarg/projects/intuit/turbotax-agent-service/src/main/python"
if main_path not in sys.path:
    sys.path.insert(0, main_path)

import pytest
from fastapi.testclient import TestClient


# Import the app within the test functions to avoid module-level import issues
def get_app():
    import importlib

    # Load the config module first
    spec = importlib.util.spec_from_file_location(
        "turbotax.agent.config",
        "/home/mgarg/projects/intuit/turbotax-agent-service/src/main/python/turbotax/agent/config.py",
    )
    module = importlib.util.module_from_spec(spec)
    sys.modules["turbotax.agent.config"] = module
    spec.loader.exec_module(module)

    # Load the base_assistant module
    spec = importlib.util.spec_from_file_location(
        "turbotax.agent.base_assistant",
        "/home/mgarg/projects/intuit/turbotax-agent-service/src/main/python/turbotax/agent/base_assistant.py",
    )
    module = importlib.util.module_from_spec(spec)
    sys.modules["turbotax.agent.base_assistant"] = module
    spec.loader.exec_module(module)

    # Load the prompts module
    spec = importlib.util.spec_from_file_location(
        "turbotax.agent.prompts",
        "/home/mgarg/projects/intuit/turbotax-agent-service/src/main/python/turbotax/agent/prompts.py",
    )
    module = importlib.util.module_from_spec(spec)
    sys.modules["turbotax.agent.prompts"] = module
    spec.loader.exec_module(module)

    # Load the ollama_assistant module
    spec = importlib.util.spec_from_file_location(
        "turbotax.agent.ollama_assistant",
        "/home/mgarg/projects/intuit/turbotax-agent-service/src/main/python/turbotax/agent/ollama_assistant.py",
    )
    module = importlib.util.module_from_spec(spec)
    sys.modules["turbotax.agent.ollama_assistant"] = module
    spec.loader.exec_module(module)

    # Load the openai_assistant module
    spec = importlib.util.spec_from_file_location(
        "turbotax.agent.openai_assistant",
        "/home/mgarg/projects/intuit/turbotax-agent-service/src/main/python/turbotax/agent/openai_assistant.py",
    )
    module = importlib.util.module_from_spec(spec)
    sys.modules["turbotax.agent.openai_assistant"] = module
    spec.loader.exec_module(module)

    # Load the models module
    spec = importlib.util.spec_from_file_location(
        "turbotax.agent.models",
        "/home/mgarg/projects/intuit/turbotax-agent-service/src/main/python/turbotax/agent/models.py",
    )
    module = importlib.util.module_from_spec(spec)
    sys.modules["turbotax.agent.models"] = module
    spec.loader.exec_module(module)

    # Load the assistants module
    spec = importlib.util.spec_from_file_location(
        "turbotax.agent.assistants",
        "/home/mgarg/projects/intuit/turbotax-agent-service/src/main/python/turbotax/agent/assistants.py",
    )
    module = importlib.util.module_from_spec(spec)
    sys.modules["turbotax.agent.assistants"] = module
    spec.loader.exec_module(module)

    # Load the constants module
    spec = importlib.util.spec_from_file_location(
        "turbotax.agent.constants",
        "/home/mgarg/projects/intuit/turbotax-agent-service/src/main/python/turbotax/agent/constants.py",
    )
    module = importlib.util.module_from_spec(spec)
    sys.modules["turbotax.agent.constants"] = module
    spec.loader.exec_module(module)

    # Load the exceptions module
    spec = importlib.util.spec_from_file_location(
        "turbotax.agent.exceptions",
        "/home/mgarg/projects/intuit/turbotax-agent-service/src/main/python/turbotax/agent/exceptions.py",
    )
    module = importlib.util.module_from_spec(spec)
    sys.modules["turbotax.agent.exceptions"] = module
    spec.loader.exec_module(module)

    # Load the dependencies module
    spec = importlib.util.spec_from_file_location(
        "turbotax.agent.dependencies",
        "/home/mgarg/projects/intuit/turbotax-agent-service/src/main/python/turbotax/agent/dependencies.py",
    )
    module = importlib.util.module_from_spec(spec)
    sys.modules["turbotax.agent.dependencies"] = module
    spec.loader.exec_module(module)

    # Load the services package
    spec = importlib.util.spec_from_file_location(
        "turbotax.agent.services",
        "/home/mgarg/projects/intuit/turbotax-agent-service/src/main/python/turbotax/agent/services/__init__.py",
    )
    module = importlib.util.module_from_spec(spec)
    sys.modules["turbotax.agent.services"] = module
    spec.loader.exec_module(module)

    # Load the refund_service module
    spec = importlib.util.spec_from_file_location(
        "turbotax.agent.services.refund_service",
        "/home/mgarg/projects/intuit/turbotax-agent-service/src/main/python/turbotax/agent/services/refund_service.py",
    )
    module = importlib.util.module_from_spec(spec)
    sys.modules["turbotax.agent.services.refund_service"] = module
    spec.loader.exec_module(module)

    # Load the streaming_service module
    spec = importlib.util.spec_from_file_location(
        "turbotax.agent.services.streaming_service",
        "/home/mgarg/projects/intuit/turbotax-agent-service/src/main/python/turbotax/agent/services/streaming_service.py",
    )
    module = importlib.util.module_from_spec(spec)
    sys.modules["turbotax.agent.services.streaming_service"] = module
    spec.loader.exec_module(module)

    # Load the query_processor module
    spec = importlib.util.spec_from_file_location(
        "turbotax.agent.services.query_processor",
        "/home/mgarg/projects/intuit/turbotax-agent-service/src/main/python/turbotax/agent/services/query_processor.py",
    )
    module = importlib.util.module_from_spec(spec)
    sys.modules["turbotax.agent.services.query_processor"] = module
    spec.loader.exec_module(module)

    # Load the routers package
    spec = importlib.util.spec_from_file_location(
        "turbotax.agent.routers",
        "/home/mgarg/projects/intuit/turbotax-agent-service/src/main/python/turbotax/agent/routers/__init__.py",
    )
    module = importlib.util.module_from_spec(spec)
    sys.modules["turbotax.agent.routers"] = module
    spec.loader.exec_module(module)

    # Load the health router
    spec = importlib.util.spec_from_file_location(
        "turbotax.agent.routers.health",
        "/home/mgarg/projects/intuit/turbotax-agent-service/src/main/python/turbotax/agent/routers/health.py",
    )
    module = importlib.util.module_from_spec(spec)
    sys.modules["turbotax.agent.routers.health"] = module
    spec.loader.exec_module(module)

    # Load the assist router
    spec = importlib.util.spec_from_file_location(
        "turbotax.agent.routers.assist",
        "/home/mgarg/projects/intuit/turbotax-agent-service/src/main/python/turbotax/agent/routers/assist.py",
    )
    module = importlib.util.module_from_spec(spec)
    sys.modules["turbotax.agent.routers.assist"] = module
    spec.loader.exec_module(module)

    # Load the main module
    spec = importlib.util.spec_from_file_location(
        "turbotax.agent.main",
        "/home/mgarg/projects/intuit/turbotax-agent-service/src/main/python/turbotax/agent/main.py",
    )
    module = importlib.util.module_from_spec(spec)
    sys.modules["turbotax.agent.main"] = module
    spec.loader.exec_module(module)

    app = module.app
    # Initialize assistants since startup event may not run in test
    from turbotax.agent.dependencies import initialize_assistants

    initialize_assistants()
    return app


@pytest.fixture
def client():
    app = get_app()
    return TestClient(app)


def test_root_endpoint(client):
    """Test the root endpoint"""
    response = client.get("/")
    assert response.status_code == 200
    data = response.json()
    assert "message" in data
    assert "TurboTax Agent Service" in data["message"]


def test_health_endpoint(client):
    """Test the health check endpoint"""
    response = client.get("/health")
    assert response.status_code == 200
    data = response.json()
    assert data["status"] == "healthy"
    assert data["service"] == "turbotax-agent-service"
    assert data["python_version"] == "3.11"


def test_capabilities_endpoint(client):
    """Test the capabilities endpoint"""
    response = client.get("/api/v1/capabilities")
    assert response.status_code == 200
    data = response.json()
    assert "tax_filing_assistance" in data
    assert data["tax_filing_assistance"] is True


def test_assist_endpoint(client):
    """Test the assist endpoint"""
    payload = {
        "user_id": "test_user_123",
        "query": "How do I file my taxes?",
        "context": {"tax_year": "2024"},
    }

    response = client.post("/api/v1/assist", json=payload)
    assert response.status_code == 200
    data = response.json()

    # Check response structure
    assert "response" in data
    assert "confidence" in data
    assert "suggestions" in data
    assert "next_steps" in data

    # Check data types
    assert isinstance(data["response"], str)
    assert isinstance(data["confidence"], float)
    assert 0 <= data["confidence"] <= 1
    assert isinstance(data["suggestions"], list)
    assert isinstance(data["next_steps"], list)


@pytest.mark.asyncio
async def test_assist_endpoint_async():
    """Test the assist endpoint with async client"""
    from httpx import AsyncClient

    app = get_app()

    # Use TestClient for FastAPI testing, even in async context
    from fastapi.testclient import TestClient

    client = TestClient(app)

    payload = {"user_id": "async_test_user", "query": "What deductions can I claim?"}

    response = client.post("/api/v1/assist", json=payload)
    assert response.status_code == 200

    data = response.json()
    assert "response" in data
    assert data["confidence"] > 0

"""
Tests for TurboTax Agent Service
"""

import sys
import os
# Add the main source path to sys.path
main_path = '/home/mgarg/projects/intuit/turbotax-agent-service/src/main/python'
if main_path not in sys.path:
    sys.path.insert(0, main_path)

import pytest
from fastapi.testclient import TestClient

# Import the app within the test functions to avoid module-level import issues
def get_app():
    import sys
    import os
    import importlib

    # Add the main source path to sys.path
    main_path = '/home/mgarg/projects/intuit/turbotax-agent-service/src/main/python'
    if main_path not in sys.path:
        sys.path.insert(0, main_path)

    # Try importing using importlib
    try:
        spec = importlib.util.spec_from_file_location("turbotax.agent.main", "/home/mgarg/projects/intuit/turbotax-agent-service/src/main/python/turbotax/agent/main.py")
        module = importlib.util.module_from_spec(spec)
        spec.loader.exec_module(module)
        app = module.app
        return app
    except Exception as e:
        print(f"Importlib failed: {e}")
        raise

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
        "context": {"tax_year": "2024"}
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

    payload = {
        "user_id": "async_test_user",
        "query": "What deductions can I claim?"
    }

    response = client.post("/api/v1/assist", json=payload)
    assert response.status_code == 200

    data = response.json()
    assert "response" in data
    assert data["confidence"] > 0
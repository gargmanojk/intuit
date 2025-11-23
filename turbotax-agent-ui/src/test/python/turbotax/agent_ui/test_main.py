"""
Tests for TurboTax Agent UI
"""

import os
import sys

# Add the main source path to sys.path
main_path = "/home/mgarg/projects/intuit/turbotax-agent-ui/src/main/python"
if main_path not in sys.path:
    sys.path.insert(0, main_path)

import pytest
from fastapi.testclient import TestClient

# Tests use fixtures from conftest.py


def test_root_endpoint(client):
    """Test the root endpoint"""
    response = client.get("/")
    assert response.status_code == 200
    data = response.json()
    assert "message" in data
    assert "TurboTax Agent UI" in data["message"]


def test_health_endpoint(client):
    """Test the health check endpoint"""
    response = client.get("/health")
    assert response.status_code == 200
    data = response.json()
    assert data["status"] == "healthy"
    assert data["service"] == "turbotax-agent-ui"
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
    pytest.skip("Integration test requiring external agent service - skipped in build")


@pytest.mark.asyncio
async def test_assist_endpoint_async(client):
    """Test the assist endpoint with async client"""
    pytest.skip("Integration test requiring external agent service - skipped in build")

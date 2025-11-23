"""
Tests for TurboTax Agent UI Web Interface
"""

import sys
import os

# Add the main source path to sys.path
main_path = "/home/mgarg/projects/intuit/turbotax-agent-ui/src/main/python"
if main_path not in sys.path:
    sys.path.insert(0, main_path)

import pytest
from fastapi.testclient import TestClient


# Tests use fixtures from conftest.py


def test_home_page(client):
    """Test the home page loads"""
    response = client.get("/web")
    assert response.status_code == 200
    content = response.text
    assert "TurboTax" in content
    assert "Welcome to TurboTax Assistant" in content


def test_health_endpoint(client):
    """Test the health check endpoint"""
    response = client.get("/health")
    assert response.status_code == 200
    data = response.json()
    assert "status" in data
    assert "service" in data
    assert data["service"] == "turbotax-agent-ui"


def test_health_api_endpoint(client):
    """Test the API health endpoint"""
    response = client.get("/api/health")
    assert response.status_code == 200
    data = response.json()
    assert "web_ui" in data
    assert "agent_service" in data


def test_static_files(client):
    """Test that static files are served"""
    response = client.get("/static/css/styles.css")
    assert response.status_code == 200
    assert "text/css" in response.headers.get("content-type", "")

    response = client.get("/static/js/app.js")
    assert response.status_code == 200
    assert "text/javascript" in response.headers.get("content-type", "")

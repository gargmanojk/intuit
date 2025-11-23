#!/usr/bin/env python3
"""
Simple test script for TurboTax Web UI
"""

import sys
import os

# Add the main source path to sys.path
sys.path.insert(0, os.path.join(os.path.dirname(__file__), "src/main/python"))

from turbotax.web_ui.main import TurboTaxWebUI


def test_web_ui_creation():
    """Test that the Web UI can be created"""
    web_ui = TurboTaxWebUI()
    app = web_ui.create_app()

    assert app is not None
    assert app.title == "TurboTax Web UI"
    print("✓ Web UI creation test passed")


def test_agent_service_url():
    """Test that agent service URL is configurable"""
    # Test default URL
    web_ui = TurboTaxWebUI()
    assert web_ui.agent_service_url == "http://localhost:9001"

    # Test custom URL via environment
    os.environ["AGENT_SERVICE_URL"] = "http://custom-host:9999"
    web_ui_custom = TurboTaxWebUI()
    assert web_ui_custom.agent_service_url == "http://custom-host:9999"

    # Clean up
    del os.environ["AGENT_SERVICE_URL"]
    print("✓ Agent service URL configuration test passed")


if __name__ == "__main__":
    print("Testing TurboTax Web UI...")
    test_web_ui_creation()
    test_agent_service_url()
    print("All tests passed! 🎉")

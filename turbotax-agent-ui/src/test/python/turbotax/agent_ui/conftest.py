"""
Pytest configuration for TurboTax Agent UI tests
"""

import os
import sys
import pytest
import importlib.util


@pytest.fixture(scope="session", autouse=True)
def setup_test_environment():
    """Set up the test environment with proper imports"""
    project_root = "/home/mgarg/projects/intuit/turbotax-agent-ui"
    main_src_path = os.path.join(project_root, "src/main/python")

    # Add to sys.path if not already there
    if main_src_path not in sys.path:
        sys.path.insert(0, main_src_path)


def load_module_from_path(module_name, file_path):
    """Load a module from a file path"""
    spec = importlib.util.spec_from_file_location(module_name, file_path)
    module = importlib.util.module_from_spec(spec)
    sys.modules[module_name] = module
    spec.loader.exec_module(module)
    return module


@pytest.fixture
def app():
    """Create and return the FastAPI app for testing"""
    # Load all dependent modules in the correct order
    project_root = "/home/mgarg/projects/intuit/turbotax-agent-ui"
    base_path = os.path.join(project_root, "src/main/python/turbotax/agent_ui")

    # Load modules in dependency order
    load_module_from_path(
        "turbotax.agent_ui.constants", os.path.join(base_path, "constants.py")
    )
    load_module_from_path(
        "turbotax.agent_ui.exceptions", os.path.join(base_path, "exceptions.py")
    )
    load_module_from_path(
        "turbotax.agent_ui.models", os.path.join(base_path, "models.py")
    )
    load_module_from_path(
        "turbotax.agent_ui.config", os.path.join(base_path, "config.py")
    )
    load_module_from_path(
        "turbotax.agent_ui.prompts", os.path.join(base_path, "prompts.py")
    )
    load_module_from_path(
        "turbotax.agent_ui.base_assistant", os.path.join(base_path, "base_assistant.py")
    )
    load_module_from_path(
        "turbotax.agent_ui.ollama_assistant",
        os.path.join(base_path, "ollama_assistant.py"),
    )
    load_module_from_path(
        "turbotax.agent_ui.openai_assistant",
        os.path.join(base_path, "openai_assistant.py"),
    )
    load_module_from_path(
        "turbotax.agent_ui.assistants", os.path.join(base_path, "assistants.py")
    )
    load_module_from_path(
        "turbotax.agent_ui.dependencies", os.path.join(base_path, "dependencies.py")
    )

    # Load service modules
    load_module_from_path(
        "turbotax.agent_ui.services.refund_service",
        os.path.join(base_path, "services/refund_service.py"),
    )
    load_module_from_path(
        "turbotax.agent_ui.services.streaming_service",
        os.path.join(base_path, "services/streaming_service.py"),
    )
    load_module_from_path(
        "turbotax.agent_ui.services.query_processor",
        os.path.join(base_path, "services/query_processor.py"),
    )

    # Load router modules
    load_module_from_path(
        "turbotax.agent_ui.routers.health", os.path.join(base_path, "routers/health.py")
    )
    load_module_from_path(
        "turbotax.agent_ui.routers.assist", os.path.join(base_path, "routers/assist.py")
    )

    # Finally load the main module
    main_module = load_module_from_path(
        "turbotax.agent_ui.main", os.path.join(base_path, "main.py")
    )

    # Initialize assistants (this is normally done in startup event)
    from turbotax.agent_ui.dependencies import initialize_assistants

    initialize_assistants()

    return main_module.app


@pytest.fixture
def client(app):
    """Create a test client for the FastAPI app"""
    from fastapi.testclient import TestClient

    return TestClient(app)

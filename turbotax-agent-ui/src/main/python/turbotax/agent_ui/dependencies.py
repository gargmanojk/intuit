# Dependencies for the TurboTax Agent Service

from typing import Dict
from .assistants import OllamaTaxAssistant, OpenAITaxAssistant
from .base_assistant import TaxAssistant
from .exceptions import ProviderNotFoundError

# Global assistants registry (initialized on startup)
_assistants: Dict[str, TaxAssistant] = {}


def get_assistant(provider: str) -> TaxAssistant:
    """Get the appropriate assistant based on the provider."""
    if provider not in _assistants:
        raise ProviderNotFoundError(provider, list(_assistants.keys()))
    return _assistants[provider]


def initialize_assistants():
    """Initialize all available assistants."""
    global _assistants
    _assistants = {}

    # Only initialize Ollama assistant (always available)
    try:
        _assistants["ollama"] = OllamaTaxAssistant()
    except Exception as e:
        print(f"Warning: Could not initialize Ollama assistant: {e}")

    # Only initialize OpenAI assistant if API key is available
    try:
        import os

        if os.getenv("OPENAI_API_KEY"):
            _assistants["openai"] = OpenAITaxAssistant()
        else:
            print(
                "Warning: OPENAI_API_KEY not set, skipping OpenAI assistant initialization"
            )
    except Exception as e:
        print(f"Warning: Could not initialize OpenAI assistant: {e}")


def get_available_providers() -> list[str]:
    """Get list of available AI providers."""
    return list(_assistants.keys())


def get_query_processor():
    """Get a query processor instance."""
    from .services.query_processor import QueryProcessor

    return QueryProcessor()


def get_refund_service():
    """Get a refund service instance."""
    from .services.refund_service import RefundService

    return RefundService()

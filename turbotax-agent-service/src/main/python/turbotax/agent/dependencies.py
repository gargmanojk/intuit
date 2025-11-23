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
        raise ProviderNotFoundError(provider)
    return _assistants[provider]


def initialize_assistants():
    """Initialize all available assistants."""
    global _assistants
    _assistants = {
        "ollama": OllamaTaxAssistant(),
        "openai": OpenAITaxAssistant(),
    }


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

# Dependencies for the TurboTax Agent Service

import os
from typing import Dict, Optional
from ..core.assistants import OllamaTaxAssistant, OpenAITaxAssistant
from ..core.assistants.base_assistant import TaxAssistant
from .exceptions import ProviderNotFoundError
from ..config import logger


class AssistantRegistry:
    """Registry for AI assistants with proper lifecycle management."""

    def __init__(self):
        self._assistants: Dict[str, TaxAssistant] = {}
        self._initialized = False

    def initialize(self):
        """Initialize available assistants."""
        if self._initialized:
            return

        # Initialize assistants
        try:
            self._assistants["ollama"] = OllamaTaxAssistant()
            logger.info("Initialized Ollama assistant")
        except Exception as e:
            logger.warning(f"Could not initialize Ollama assistant: {e}")

        try:
            if os.getenv("OPENAI_API_KEY"):
                self._assistants["openai"] = OpenAITaxAssistant()
                logger.info("Initialized OpenAI assistant")
            else:
                logger.info(
                    "OpenAI API key not set, skipping OpenAI assistant initialization"
                )
        except Exception as e:
            logger.warning(f"Could not initialize OpenAI assistant: {e}")

        self._initialized = True
        logger.info(
            f"Initialized {len(self._assistants)} assistants: {list(self._assistants.keys())}"
        )

    def get_assistant(self, provider: str) -> TaxAssistant:
        """Get assistant by provider."""
        if not self._initialized:
            self.initialize()

        if provider not in self._assistants:
            raise ProviderNotFoundError(provider, list(self._assistants.keys()))
        return self._assistants[provider]

    def get_available_providers(self) -> list[str]:
        """Get list of available providers."""
        if not self._initialized:
            self.initialize()
        return list(self._assistants.keys())


# Global instance for backward compatibility
_registry = AssistantRegistry()


def get_assistant(provider: str) -> TaxAssistant:
    return _registry.get_assistant(provider)


def initialize_assistants():
    _registry.initialize()


def get_available_providers() -> list[str]:
    return _registry.get_available_providers()


def get_query_processor():
    """Get a query processor instance."""
    from ..services.query_processor import QueryProcessor

    return QueryProcessor()


def get_refund_service():
    """Get a refund service instance."""
    from ..services.refund_service import RefundService

    return RefundService()

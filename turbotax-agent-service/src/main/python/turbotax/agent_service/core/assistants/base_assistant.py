from abc import ABC, abstractmethod
from typing import Optional, Dict, Any, AsyncGenerator
from ...interfaces import TaxAssistantInterface
from ...config import logger


class TaxAssistant(TaxAssistantInterface, ABC):
    """Base class for tax assistance AI providers."""

    @abstractmethod
    def generate_response(
        self, query: str, context: Optional[Dict[str, Any]] = None
    ) -> str:
        """Generate a response for the given query."""
        pass

    @abstractmethod
    def generate_streaming_response(
        self, query: str, context: Optional[Dict[str, Any]] = None
    ) -> AsyncGenerator[str, None]:
        """Generate a streaming response for the given query."""
        pass

from abc import ABC, abstractmethod
from typing import Any, Dict, AsyncGenerator, Optional


class TaxAssistantInterface(ABC):
    """Interface for tax assistance AI providers."""

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